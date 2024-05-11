package pposonggil.usedStuff.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;

    @Test
    public void 전체_게시글_조회() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        // 게시글 1, 2 작성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = false;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);
        Long boardId2 = boardService.createBoard(savedId2, title, content, startTime, endTime, address, price, isFreebie);

        // when
        Member savedWriter1 = memberService.findOne(savedId1);
        Board savedBoard1 = boardService.findOne(boardId1);

        Member savedWriter2 = memberService.findOne(savedId2);
        Board savedBoard2 = boardService.findOne(boardId2);

        // then
        List<Board> boards = boardService.findBoards();
        assertEquals(2, boards.size());
        assertEquals(savedWriter1, savedBoard1.getWriter());
        assertEquals(savedWriter2, savedBoard2.getWriter());
    }

    @Test
    public void 작성자_정보와_함께_모든_게시글_조회() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 작성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = false;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 게시글 2 작성
        String title2 = "title2";
        String content2 = "우산 팔아요2";
        LocalDateTime startTime2 = startTime.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusMinutes(30);
        TransactionAddress address2 = new TransactionAddress("숭숭숭", 37.4500, 126.9500, "주소2");
        Long price2 = 2000L;
        boolean isFreebie2 = false;

        Long boardId2 = boardService.createBoard(savedId1, title2, content2, startTime2, endTime2, address2, price2, isFreebie2);

        // when
        Member savedWriter = memberService.findOne(savedId1);
        List<Board> boards = boardService.findAllWithMember();

        // then
        assertEquals(2, boards.size());

        Board board1 = boards.stream().filter(board -> board.getId().equals(boardId1)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        assertNotNull(board1);
        assertEquals(savedWriter, board1.getWriter());
        assertEquals(title, board1.getTitle());
        assertEquals(content, board1.getContent());
        assertEquals(startTime, board1.getStartTime());
        assertEquals(endTime, board1.getEndTime());
        assertEquals(address, board1.getAddress());
        assertEquals(price, board1.getPrice());
        assertFalse(board1.isFreebie());

        Board board2 = boards.stream().filter(board -> board.getId().equals(boardId2)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        assertNotNull(board2);
        assertEquals(savedWriter, board2.getWriter());
        assertEquals(title2, board2.getTitle());
        assertEquals(content2, board2.getContent());
        assertEquals(startTime2, board2.getStartTime());
        assertEquals(endTime2, board2.getEndTime());
        assertEquals(address2, board2.getAddress());
        assertEquals(price2, board2.getPrice());
        assertFalse(board2.isFreebie());
    }

    @Test
    public void 게시판_작성() throws Exception {
        //given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 작성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = false;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // when

        // then
        Member savedWriter = memberService.findOne(savedId1);
        Board savedBoard = boardService.findOne(boardId1);

        assertNotNull(savedBoard);
        assertEquals(savedWriter, savedBoard.getWriter());
        assertEquals(title, savedBoard.getTitle());
        assertEquals(content, savedBoard.getContent());
        assertEquals(startTime, savedBoard.getStartTime());
        assertEquals(endTime, savedBoard.getEndTime());
        assertEquals(address, savedBoard.getAddress());
        assertEquals(price, savedBoard.getPrice());
        assertFalse(savedBoard.isFreebie());
    }

    @Test
    public void 게시글_수정() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 작성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = false;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 업데이트할 게시글 내용
        String updateTitle = "updateTitle";
        String updateContent = "updateContent";
        LocalDateTime updateStartTime = startTime.plusHours(1);
        LocalDateTime updateEndTime = updateStartTime.plusMinutes(30);
        TransactionAddress updateAddress = new TransactionAddress("숭숭숭", 37.5000, 126.9555, "주소2");
        Long updatePrice = 10000L;
        boolean updateIsFreebie = false;

        // when
        Member savedWriter = memberService.findOne(savedId1);
        boardService.updateBoard(boardId1, updateTitle, updateContent, updateStartTime, updateEndTime,
                updateAddress, updatePrice, updateIsFreebie);

        // then
        Board updateBoard = boardService.findOne(boardId1);

        assertNotNull(updateBoard);
        assertEquals(savedWriter, updateBoard.getWriter());
        assertEquals(updateTitle, updateBoard.getTitle());
        assertEquals(updateContent, updateBoard.getContent());
        assertEquals(updateStartTime, updateBoard.getStartTime());
        assertEquals(updateEndTime, updateBoard.getEndTime());
        assertEquals(updateAddress, updateBoard.getAddress());
        assertEquals(updatePrice, updateBoard.getPrice());
        assertFalse(updateBoard.isFreebie());
    }

    @Test
    public void 게시판_삭제() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 작성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = false;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // when
        boardService.deleteBoard(savedId1);

        // then
        assertThrows(NoSuchElementException.class, () -> boardService.findOne(savedId1));

    }

}