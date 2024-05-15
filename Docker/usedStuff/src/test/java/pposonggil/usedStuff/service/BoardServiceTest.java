package pposonggil.usedStuff.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.dto.BoardDto;
import pposonggil.usedStuff.dto.MemberDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private Long memberId1, memberId2;
    private Long boardId1, boardId2;

    @BeforeEach
    public void setUp() {
        // 회원 1, 2 생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");

        // 게시글 1, 2 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);
    }

    @Test
    public void 전체_게시글_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Board board1 = boardService.findOne(boardId1);

        Member member2 = memberService.findOne(memberId2);
        Board board2 = boardService.findOne(boardId2);

        // then
        List<Board> boards = boardService.findBoards();
        assertEquals(2, boards.size());
        assertEquals(member1, board1.getWriter());
        assertEquals(member2, board2.getWriter());
    }

    @Test
    public void 작성자_정보와_함께_모든_게시글_조회() throws Exception {
        // when
        Board board1 = boardService.findOne(boardId1);
        Board board2 = boardService.findOne(boardId2);

        // then
        List<Board> boards = boardService.findAllWithMember();
        assertEquals(2, boards.size());

        Board findBoard1 = boards.stream().filter(board -> board.getId().equals(boardId1)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        assertNotNull(board1);
        assertEquals(findBoard1.getWriter(), board1.getWriter());
        assertEquals(findBoard1.getTitle(), board1.getTitle());
        assertEquals(findBoard1.getContent(), board1.getContent());
        assertEquals(findBoard1.getStartTime(), board1.getStartTime());
        assertEquals(findBoard1.getEndTime(), board1.getEndTime());
        assertEquals(findBoard1.getAddress(), board1.getAddress());
        assertEquals(findBoard1.getPrice(), board1.getPrice());
        assertFalse(findBoard1.isFreebie());

        Board findBoard2 = boards.stream().filter(board -> board.getId().equals(boardId2)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        assertNotNull(board2);
        assertEquals(findBoard2.getWriter(), board2.getWriter());
        assertEquals(findBoard2.getTitle(), board2.getTitle());
        assertEquals(findBoard2.getContent(), board2.getContent());
        assertEquals(findBoard2.getStartTime(), board2.getStartTime());
        assertEquals(findBoard2.getEndTime(), board2.getEndTime());
        assertEquals(findBoard2.getAddress(), board2.getAddress());
        assertEquals(findBoard2.getPrice(), board2.getPrice());
        assertFalse(findBoard2.isFreebie());
    }

    @Test
    public void 게시판_작성() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Board board1 = boardService.findOne(boardId1);

        // then
        assertNotNull(board1);
        assertEquals(member1, board1.getWriter());
    }

    @Test
    public void 게시글_수정() throws Exception {
        // given
        // 업데이트할 게시글 내용
        String updateTitle = "제목1";
        String updateContent = "우산 팔아요1";
        LocalDateTime updateStartTime = LocalDateTime.now().plusHours(1);
        LocalDateTime updateEndTime = updateStartTime.plusMinutes(30);
        TransactionAddress updateAddress = new TransactionAddress("숭숭숭", 37.5000, 126.9555, "주소2");
        Long updatePrice = 10000L;
        boolean updateIsFreebie = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String updateFormatStartTime = updateStartTime.format(formatter);
        String updateFormatEndTime = updateEndTime.format(formatter);

        // when
        Member member1 = memberService.findOne(memberId1);
        BoardDto boardDto = BoardDto.builder()
                .boardId(boardId1)
                .writerId(memberId1)
                .title(updateTitle)
                .content(updateContent)
                .startTimeString(updateFormatStartTime)
                .endTimeString(updateFormatEndTime)
                .address(updateAddress)
                .price(updatePrice)
                .isFreebie(updateIsFreebie)
                .build();

        boardService.updateBoard(boardDto);

        // then
        Board updateBoard = boardService.findOne(boardId1);

        assertNotNull(updateBoard);
        assertEquals(member1, updateBoard.getWriter());
        assertEquals(updateTitle, updateBoard.getTitle());
        assertEquals(updateContent, updateBoard.getContent());
        assertEquals(updateFormatStartTime, updateBoard.getStartTime().format(formatter));
        assertEquals(updateFormatEndTime, updateBoard.getEndTime().format(formatter));
        assertEquals(updateAddress, updateBoard.getAddress());
        assertEquals(updatePrice, updateBoard.getPrice());
        assertFalse(updateBoard.isFreebie());
    }

    @Test
    public void 게시판_삭제() throws Exception {
        // when
        boardService.deleteBoard(boardId1);
        List<Board> boards = boardService.findBoards();

        // then
        assertEquals(1, boards.size());
        assertThrows(NoSuchElementException.class, () -> boardService.findOne(memberId1));
    }

    public Long createMember(String name, String nickName, String phone) {
        MemberDto memberDto = MemberDto.builder()
                .name(name)
                .nickName(nickName)
                .phone(phone)
                .build();

        return memberService.createMember(memberDto);
    }


    public Long createBoard(Long savedId, String title, String content, LocalDateTime startTime, LocalDateTime endTime,
                            TransactionAddress address, Long price, boolean isFreebie) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String formatStartTime = startTime.format(formatter);
        String formatEndTime = endTime.format(formatter);

        BoardDto boardDto = BoardDto.builder()
                .writerId(savedId)
                .title(title)
                .content(content)
                .startTimeString(formatStartTime)
                .endTimeString(formatEndTime)
                .address(address)
                .price(price)
                .isFreebie(isFreebie)
                .build();
        return boardService.createBoard(boardDto);
    }
}