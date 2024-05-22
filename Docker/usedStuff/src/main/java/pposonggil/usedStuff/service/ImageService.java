package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Image;
import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.image.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private final BoardRepository boardRepository;
    private final AwsS3 awsS3;
    private final ImageRepository imageRepository;

    public List<Image> findImages() {
        return imageRepository.findAll();
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
     *
     * @param imageId : 사진 아이디
     * @return 사진
     */
    public Image findOne(Long imageId) {
        Optional<Image> imageOptional = imageRepository.findById(imageId);
        if (imageOptional.isPresent())
            return imageOptional.get();
        else throw new NoSuchElementException("해당 이미지가 존재하지 않습니다.");
    }

    /**
     * 게시글 아이디로 이미지 조회
     *
     * @param boardId 조회할 게시글 아이디
     * @return 게시글 아이디가 일치하는 이미지 리스트
     */
    public List<Image> findByBoardId(Long boardId) {
        return imageRepository.findImagesByImageBoardId(boardId);
    }

    /**
     * 게시글 정보를 포함한 이미지 조회
     *
     * @return 게시글 아이디가 일치하는 이미지 리스트
     */
    public List<Image> findAllWithBoard() {
        return imageRepository.findAllWithBoard();
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
}
