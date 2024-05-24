package pposonggil.usedStuff.api.Board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.service.Board.BoardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    /**
     * 전체 게시글 조회
     * @return 게시글 Dto 리스트
     */
    @GetMapping("/api/boards")
    public List<BoardDto> boards() {
        return boardService.findBoards();
    }

    /**
     * 특정 게시글 상세 조회
     * @param boardId : 조회할 게시글 아이디
     * @return 게시글 아이디로 조회한 게시글 Dto
     */
    @GetMapping("/api/board/{boardId}")
    public BoardDto getBoardByBoardId(@PathVariable Long boardId) {
        return boardService.findOne(boardId);
    }

    /**
     * 작성자 아이디로 게시글 조회
     * @param memberId : 조회할 회원 아이디
     * @return 회원 아이디로 조회한 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/by-member/{memberId}")
    public List<BoardDto> findImageBoardsByWriterId(@PathVariable Long memberId) {
        return boardService.findImageBoardsByWriterId(memberId);
    }

    /**
     * 작성자 & 이미지 & 게시글 조회
     * @return 이미지 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/with-member-image")
    public List<BoardDto> getBoardsWithMember() {
        return boardService.findAllWithMember();
    }

    /**
     * 게시글 작성
     * @param boardDto : 작성할 내용이 담긴 게시글 Dto
     * @return 성공 -->
     *          "boardId" : [Id]
     *          "message" : "게시글을 작성을 완료했습니다."
     */
    @PostMapping("/api/board")
    public ResponseEntity<Object> createBoard(@RequestBody BoardDto boardDto) {
        Long boardId = boardService.createBoard(boardDto);

        Map<String, Object> response = new HashMap<>();
        response.put("boardId", boardId);
        response.put("message", "게시글을 작성을 완료했습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정
     * @param boardId : 게시글 아이디
     * @param boardDto : 수정한 게시글 Dto
     * @return 성공 -->
     *          "boardId" : [Id]
     *          "message" : "게시글을 수정을 완료했습니다."
     */
    @PutMapping("/api/board/{boardId}")
    public ResponseEntity<Object> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto) {
        BoardDto oldBoardDto = boardService.findOne(boardId);
        if (oldBoardDto == null) {
            return ResponseEntity.notFound().build();
        }
        boardService.updateBoard(boardDto);

        Map<String, Object> response = new HashMap<>();
        response.put("boardId", boardId);
        response.put("message", "게시글 수정을 완료했습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제
     * @param boardId : 게시글 아이디
     * @return 성공 --> "게시글을 삭제하였습니다."
     */
    @DeleteMapping("/api/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);

        return ResponseEntity.ok("게시글을 삭제하였습니다.");
    }

}
