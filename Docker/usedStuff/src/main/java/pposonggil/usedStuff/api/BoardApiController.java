package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.dto.BoardDto;
import pposonggil.usedStuff.service.BoardService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    /**
     * 전체 게시글 조회
     */
    @GetMapping("/api/boards")
    public List<BoardDto> boards() {
        List<Board> boards = boardService.findBoards();

        return boards.stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시글 상세 조회
     */
    @GetMapping("/api/board/{boardId}")
    public BoardDto getBoarByBoardId(@PathVariable Long boardId) {
        Board board = boardService.findOne(boardId);
        return BoardDto.fromEntity(board);
    }

    /**
     * 작성자 아이디로 게시글 조회
     */
    @GetMapping("/api/boards/{memberId}")
    public List<BoardDto> getBoardsByMemberId(@PathVariable Long memberId) {
        List<Board> boards = boardService.findBoardsByWriterId(memberId);
        return boards.stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 & 작성자 조회
     */
    @GetMapping("/api/boards/with-member")
    public List<BoardDto> getBoardsWithMember() {
        List<Board> boards = boardService.findAllWithMember();
        return boards.stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 작성
     */
    @PostMapping("/api/board")
    public ResponseEntity<String> createBoard(@RequestBody BoardDto boardDto) {
        Long boardId = boardService.createBoard(boardDto);
        return ResponseEntity.ok("Created board with ID: " + boardId);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/api/board/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto) {
        Board board = boardService.findOne(boardId);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }

        boardService.updateBoard(boardDto);
        return ResponseEntity.ok("게시글을 수정하였습니다.");
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/api/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);

        return ResponseEntity.ok("게시글을 삭제하였습니다.");
    }

}
