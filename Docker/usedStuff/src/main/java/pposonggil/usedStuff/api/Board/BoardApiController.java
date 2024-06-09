package pposonggil.usedStuff.api.Board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Board.BoardService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;
    private final ValidateService validateService;


    /**
     * 전체 게시글 조회
     *
     * @return 게시글 Dto 리스트
     */
    @GetMapping("/api/boards")
    public List<BoardDto> boards()
    {
        return boardService.findBoardsByMember(validateService.getMyId());
    }

    /**
     * 예상 강수량 정보를 포함한 게시글 조회
     *
     * @param startDto : 출발지 정보
     * @param memberId : 회원 아이디
     * @return : 강수량 정보가 포함된 게시글 Dto 리스트
     * @throws IOException
     */
    @PostMapping("/api/boards/with-expected-rain/{memberId}")
    public List<BoardDto> getBoardsWithExpectedRain(@RequestPart("startDto") PointInformationDto startDto,
                                                    @PathVariable Long memberId) throws IOException
    {
        return boardService.findBoardsWithExpectedRain(startDto, memberId);
    }

    /**
     * 특정 게시글 상세 조회
     * 기상정보를 포함한다
     *
     * @param boardId : 조회할 게시글 아이디
     * @return 게시글 아이디로 조회한 게시글 Dto
     */
    @GetMapping("/api/board/by-board/{boardId}")
    public BoardDto getBoardByBoardId(@PathVariable Long boardId) {
        return boardService.findOne(boardId);
    }

    /**
     * 작성자 아이디로 게시글 조회
     *
     * @param memberId : 조회할 회원 아이디
     * @return 회원 아이디로 조회한 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/by-member/{memberId}")
    public List<BoardDto> findBoardsByWriterId(@PathVariable Long memberId) {
        return boardService.findBoardsByWriterId(memberId);
    }

    /**
     * 작성자 & 게시글 조회
     *
     * @return 게시글 Dto 리스트
     */
    @GetMapping("/api/boards/with-member")
    public List<BoardDto> getBoardsWithMember() {
        return boardService.findAllWithMember();
    }

    /**
     * 본인
     * 게시글 작성
     *
     * @param boardDto : 작성할 내용이 담긴 게시글 Dto
     * @return 성공 -->
     * "boardId" : [Id]
     * "message" : "게시글을 작성을 완료했습니다."
     */
    @PostMapping("/api/board")
    public ResponseEntity<Object> createBoard(@RequestPart("boardDto") BoardDto boardDto,
                                              @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        validateService.validateMemberIdAndThrow(boardDto.getWriterId());
        Long boardId = boardService.createBoard(boardDto, file);

        Map<String, Object> response = new HashMap<>();
        response.put("boardId", boardId);
        response.put("message", "게시글을 작성을 완료했습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 본인
     * 게시글 수정
     *
     * @param boardId  : 게시글 아이디
     * @param boardDto : 수정한 게시글 Dto
     * @param file     : 수정할 사진 파일 (선택 사항)
     * @return 성공 -->
     * "boardId" : [Id]
     * "message" : "게시글을 수정을 완료했습니다."
     */
    @PutMapping("/api/board/{boardId}")
    public ResponseEntity<Object> updateBoard(@PathVariable Long boardId,
                                              @RequestPart("boardDto") BoardDto boardDto,
                                              @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        validateService.validateMemberIdAndThrow(boardDto.getWriterId());
        BoardDto oldBoardDto = boardService.findOne(boardId);
        if (oldBoardDto == null) {
            return ResponseEntity.notFound().build();
        }
        boardService.updateBoard(boardDto, file);

        Map<String, Object> response = new HashMap<>();
        response.put("boardId", boardId);
        response.put("message", "게시글 수정을 완료했습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 본인
     * 게시글 삭제
     *
     * @param boardId : 게시글 아이디
     * @return 성공 --> "게시글을 삭제하였습니다."
     */
    @DeleteMapping("/api/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        BoardDto boardDto =  boardService.findOne(boardId);
        Long writerId =  boardDto.getWriterId();

        validateService.checkAdminMemberIdAndThrow(writerId);
        
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok("게시글을 삭제하였습니다.");
    }

}
