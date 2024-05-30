package pposonggil.usedStuff.api.Board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Board.BoardImagesDto;
import pposonggil.usedStuff.service.Board.BoardImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardImageApiController {
    private final BoardImageService boardImageService;

    /**
     * 전체 이미지 포함한 게시글 조회
     * @return 이미지 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/with-image")
    public List<BoardImagesDto> boards() {
        return boardImageService.findBoardsWithImages();
    }

    /**
     * 특정 게시글을 이미지를 포함하여 상세 조회
     * @param boardId : 조회할 게시글 아이디
     * @return 게시글 아이디로 조회한 이미지 게시글 Dto
     */
    @GetMapping("/api/board/with-images/by-board/{boardId}")
    public BoardImagesDto getBoardWithMImagesByBoardId(@PathVariable Long boardId) {
        return boardImageService.findOne(boardId);
    }

    /**
     * 작성자 아이디로 이미지를 포함한 게시글 조회
     * @param memberId : 조회할 회원 아이디
     * @return 회원 아이디로 조회한 이미지 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/with-images/by-member/{memberId}")
    public List<BoardImagesDto> findImageBoardsWithMemberByWriterId(@PathVariable Long memberId) {
        return boardImageService.findBoardsWithImagesByWriterId(memberId);
    }

    /**
     * 작성자 & 이미지 & 게시글 조회
     * @return 이미지 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/with-mages")
    public List<BoardImagesDto> getBoardsWithMember() {
        return boardImageService.findAllWithMemberImages();
    }
}
