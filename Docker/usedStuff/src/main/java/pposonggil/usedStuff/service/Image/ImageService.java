package pposonggil.usedStuff.service.Image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Image;
import pposonggil.usedStuff.dto.Image.ImageDto;
import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.image.ImageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private final BoardRepository boardRepository;
    private final AwsS3 awsS3;
    private final ImageRepository imageRepository;

    public List<ImageDto> findImages() {
        List<Image> images = imageRepository.findAll();
        return images.stream()
                .map(ImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 등록
     *
     * @param boardId  : 게시글 아이디
     * @param file     : 사진 파일
     * @param filePath : 사진 파일 경로
     * @return 이미지 아이디
     * @throws IOException
     */
    @Transactional
    public Long uploadImage(Long boardId, MultipartFile file, String filePath) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(NoSuchElementException::new);
        String imageUrl = awsS3.uploadFileToS3(file, filePath);
        String fileName = file.getOriginalFilename();

        Image image = new Image(board);

        image.setImageUrl(imageUrl);
        image.setFileName(fileName);

        image.setImageBoard(board);
        imageRepository.save(image);

        return image.getId();
    }

    /**
     * 이미지 상세 조회
     * @param imageId : 사진 아이디
     * @return 이미지 Dto
     */
    public ImageDto findOne(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(NoSuchElementException::new);

        return ImageDto.fromEntity(image);
    }

    /**
     * 게시글 아이디로 이미지 조회
     * @param boardId 조회할 게시글 아이디
     * @return 게시글 아이디가 일치하는 이미지 Dto 리스트
     */
    public List<ImageDto> findByBoardId(Long boardId) {
        List<Image> images = imageRepository.findImagesByImageBoardId(boardId);
        return images.stream()
                .map(ImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 정보를 포함한 이미지 조회
     *
     * @return 게시글 아이디가 일치하는 이미지 리스트
     */
    public List<ImageDto> findAllWithBoard() {
        List<Image> images = imageRepository.findAllWithBoard();
        return images.stream()
                .map(ImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 사진 삭제
     *
     * @param imageId : 사진 아이디
     */
    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(NoSuchElementException::new);
        try {
            awsS3.deleteS3(image.getImageUrl());
        } catch (Exception e) {
            throw new RuntimeException("S3에서 파일을 삭제하는 중 오류가 발생했습니다.", e);
        }
        imageRepository.delete(image);
    }

    @Component
    @Slf4j
    static
    class AwsS3 {
        @Autowired
        AmazonS3Client amazonS3Client;
        @Value("${cloud.aws.s3.bucket}")
        private String bucket;

        /**
         * 로컬 경로에 저장
         */
        public String uploadFileToS3(MultipartFile multipartFile, String filePath) {
            // MultiPartFile --> File 로 변환
            File uploadFile = null;
            try {
                uploadFile = convert(multipartFile)
                        .orElseThrow(() -> new IllegalArgumentException("[error] : MultipartFile --> 파일 변환 실패"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // S3에 저장된 파일 이름
            String fileName = filePath + "/" + UUID.randomUUID();

            // S3에 업로드 후 로컬 파일 삭제
            String uploadImageUrl = putS3(uploadFile, fileName);
            removeNewFile(uploadFile);
            return uploadImageUrl;
        }

        /**
         * S3으로 업로드
         *
         * @param uploadFile : 업로드할 파일
         * @param fileName   : 업로드할 파일 이름
         * @return 업로드 경로
         */
        public String putS3(File uploadFile, String fileName) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                    CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, fileName).toString();
        }

        /**
         * S3에 있는 파일 삭제
         * 영어 파일만 삭제 가능 --> 한글 이름 파일은 안됨
         */
        public void deleteS3(String filePath) throws Exception {
            String key = filePath.substring(58);

            try {
                amazonS3Client.deleteObject(bucket, key);
            } catch (AmazonS3Exception e) {
                log.info(e.getErrorMessage());
            } catch (Exception exception) {
                log.info(exception.getMessage());
            }
            log.info("[S3Uploader] : S3에 있는 파일 삭제");
        }

        /**
         * 로컬에 저장된 파일 지우기
         *
         * @param targetFile : 저장된 파일
         */
        private void removeNewFile(File targetFile) {
            if (targetFile.delete()) {
                log.info("[파일 업로드] : 파일 삭제 성공");
                return;
            }
            log.info("[파일 업로드] : 파일 삭제 실패");
        }

        /**
         * 로컬에 파일 업로드 및 변환
         *
         * @param file : 업로드할 파일
         */
        private Optional<File> convert(MultipartFile file) throws IOException {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            String fileName = File.separator + originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "-";

            try {
                // 유니크한 파일명 -> createTempFile 중복방지: 자체적으로 난수 생성
                File convertFile = File.createTempFile(fileName, fileExtension);
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                return Optional.of(convertFile);
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }
}
