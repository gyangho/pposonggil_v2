package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pposonggil.usedStuff.dto.Image.ImageDto;
import pposonggil.usedStuff.service.ImageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageApiController {
    public final ImageService imageService;

    /**
     * 전체 이미지 조회
     *
     * @return 이미지 Dto 리스트
     */
    @GetMapping("/api/images")
    public List<ImageDto> images() {
        return imageService.findImages();
    }

    /**
     * 이미지 아이디로 이미지 조회
     *
     * @param imageId : 조회할 이미지 아이디
     * @return 조회한 이미지 Dto
     */
    @GetMapping("api/image/by-image/{imageId}")
    public ImageDto getImageByImageId(@PathVariable Long imageId) {
        return imageService.findOne(imageId);
    }

    /**
     * 게시글 & 이미지 조회
     *
     * @return 이미지 Dto 리스트
     */
    @GetMapping("/api/images/with-boards")
    public List<ImageDto> getImagesWithBoard() {
        return imageService.findAllWithBoard();
    }

    /**
     * 이미지 등록
     *
     * @param boardId : 사진을 등록하려는 게시글 아이디
     * @param name    : 사진을 저장할 위치 이름
     * @param file    : 사진 파일
     * @return 성공 --> "이미지를 등록하였습니다. ID : " + imageId
     * @throws IOException
     */
    @PostMapping(path = "/api/image/{boardId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> imageDto(
            @PathVariable Long boardId,
            @RequestPart(value = "name") String name,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        Long imageId = imageService.uploadImage(boardId, file, name);
        return ResponseEntity.ok("이미지를 등록하였습니다. ID : " + imageId);
    }

    /**
     * 이미지 삭제
     *
     * @param imageId : 삭제할 이미지 아이디
     * @return 성공 --> "이미지를 삭제하였습니다. ID : " + imageId
     */
    @DeleteMapping("/api/image/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);

        return ResponseEntity.ok("이미지를 삭제하였습니다. ID : " + imageId);
    }


}
