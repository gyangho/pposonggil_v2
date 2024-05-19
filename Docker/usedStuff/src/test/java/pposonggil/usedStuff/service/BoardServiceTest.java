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
import java.util.Optional;

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
        Member member2 = memberService.findOne(memberId2);
        Board board1 = boardService.findOne(boardId1);
        Board board2 = boardService.findOne(boardId2);

        // then
        List<Board> boards = boardService.findBoards();
        assertEquals(2, boards.size());

        boards.stream()
                .filter(board -> board.getWriter().equals(member1))
                .findFirst()
                .ifPresent(board -> {
                    assertAll("게시글 조회 검증(게시글1)",
                            () -> assertEquals(member1.getName(), board.getWriter().getName(), "작성자 이름 불일치"),
                            () -> assertEquals(member1.getNickName(), board.getWriter().getNickName(), "작성자 이름 불일치"),
                            () -> assertEquals(member1.getPhone(), board.getWriter().getPhone(), "작성자 이름 불일치"),
                            () -> assertEquals(board1.getTitle(), board.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals(board1.getContent(), board.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals(board1.getStartTime(), board.getStartTime(), "게시글 시작 시각 불일치"),
                            () -> assertEquals(board1.getEndTime(), board.getEndTime(), "게시글 종료 시각 불일치"),
                            () -> assertEquals(board1.getStartTimeString(), board.getStartTimeString(), "게시글 시작 시각(String) 불일치"),
                            () -> assertEquals(board1.getEndTimeString(), board.getEndTimeString(), "게시글 종료 시각(String) 불일치"),
                            () -> assertEquals(board1.getAddress(), board.getAddress(), "게시글 주소 불일치"),
                            () -> assertEquals(board1.getPrice(), board.getPrice(), "게시글 가격 불일치"),
                            () -> assertEquals(board1.isFreebie(), board.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        boards.stream()
                .filter(board -> board.getWriter().equals(member2))
                .findFirst()
                .ifPresent(board -> {
                    assertAll("게시글 조회 검증(게시글2)",
                            () -> assertEquals(member2.getName(), board.getWriter().getName(), "작성자 이름 불일치"),
                            () -> assertEquals(member2.getNickName(), board.getWriter().getNickName(), "작성자 이름 불일치"),
                            () -> assertEquals(member2.getPhone(), board.getWriter().getPhone(), "작성자 이름 불일치"),
                            () -> assertEquals(member2.getPhone(), board.getWriter().getPhone(), "작성자 이름 불일치"),
                            () -> assertEquals(board2.getTitle(), board.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals(board2.getContent(), board.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals(board2.getStartTime(), board.getStartTime(), "게시글 시작 시각 불일치"),
                            () -> assertEquals(board2.getEndTime(), board.getEndTime(), "게시글 종료 시각 불일치"),
                            () -> assertEquals(board2.getStartTimeString(), board.getStartTimeString(), "게시글 시작 시각(String) 불일치"),
                            () -> assertEquals(board2.getEndTimeString(), board.getEndTimeString(), "게시글 종료 시각(String) 불일치"),
                            () -> assertEquals(board2.getAddress(), board.getAddress(), "게시글 주소 불일치"),
                            () -> assertEquals(board2.getPrice(), board.getPrice(), "게시글 가격 불일치"),
                            () -> assertEquals(board2.isFreebie(), board.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 작성자_정보와_함께_모든_게시글_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);

        Board board1 = boardService.findOne(boardId1);
        Board board2 = boardService.findOne(boardId2);

        // then
        List<Board> boards = boardService.findAllWithMember();
        assertEquals(2, boards.size());

        boards.stream()
                .filter(board -> board.getId().equals(boardId1))
                .findFirst()
                .ifPresent(board -> {
                    assertAll("작성자 정보를 포함한 게시글 조회 검증(게시글1)",
                            () -> assertEquals(member1.getName(), board.getWriter().getName(), "작성자 이름 불일치"),
                            () -> assertEquals(member1.getNickName(), board.getWriter().getNickName(), "작성자 이름 불일치"),
                            () -> assertEquals(member1.getPhone(), board.getWriter().getPhone(), "작성자 이름 불일치"),
                            () -> assertEquals(board1.getTitle(), board.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals(board1.getContent(), board.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals(board1.getStartTime(), board.getStartTime(), "게시글 시작 시각 불일치"),
                            () -> assertEquals(board1.getEndTime(), board.getEndTime(), "게시글 종료 시각 불일치"),
                            () -> assertEquals(board1.getStartTimeString(), board.getStartTimeString(), "게시글 시작 시각(String) 불일치"),
                            () -> assertEquals(board1.getEndTimeString(), board.getEndTimeString(), "게시글 종료 시각(String) 불일치"),
                            () -> assertEquals(board1.getAddress(), board.getAddress(), "게시글 주소 불일치"),
                            () -> assertEquals(board1.getPrice(), board.getPrice(), "게시글 가격 불일치"),
                            () -> assertEquals(board1.isFreebie(), board.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        boards.stream()
                .filter(board -> board.getWriter().equals(member2))
                .findFirst()
                .ifPresent(board -> {
                    assertAll("작성자 정보를 포함한 게시글 조회 검증(게시글2)",
                            () -> assertEquals(member2.getName(), board.getWriter().getName(), "작성자 이름 불일치"),
                            () -> assertEquals(member2.getNickName(), board.getWriter().getNickName(), "작성자 이름 불일치"),
                            () -> assertEquals(member2.getPhone(), board.getWriter().getPhone(), "작성자 이름 불일치"),
                            () -> assertEquals(member2.getPhone(), board.getWriter().getPhone(), "작성자 이름 불일치"),
                            () -> assertEquals(board2.getTitle(), board.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals(board2.getContent(), board.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals(board2.getStartTime(), board.getStartTime(), "게시글 시작 시각 불일치"),
                            () -> assertEquals(board2.getEndTime(), board.getEndTime(), "게시글 종료 시각 불일치"),
                            () -> assertEquals(board2.getStartTimeString(), board.getStartTimeString(), "게시글 시작 시각(String) 불일치"),
                            () -> assertEquals(board2.getEndTimeString(), board.getEndTimeString(), "게시글 종료 시각(String) 불일치"),
                            () -> assertEquals(board2.getAddress(), board.getAddress(), "게시글 주소 객체 불일치"),
                            () -> assertEquals(board2.getAddress().getName(), board.getAddress().getName(), "게시글 주소 이름 불일치"),
                            () -> assertEquals(board2.getAddress().getLatitude(), board.getAddress().getLatitude(), "게시글 주소 위도 불일치"),
                            () -> assertEquals(board2.getAddress().getLongitude(), board.getAddress().getLongitude(), "게시글 주소 경도 불일치"),
                            () -> assertEquals(board2.getAddress().getStreet(), board.getAddress().getStreet(), "게시글 주소 도로명 불일치"),
                            () -> assertEquals(board2.getPrice(), board.getPrice(), "게시글 가격 불일치"),
                            () -> assertEquals(board2.isFreebie(), board.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 게시글_작성() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Board board1 = boardService.findOne(boardId1);

        // then
        Optional.of(board1)
                .filter(board -> board.getWriter().equals(member1))
                .ifPresent(board -> assertAll("게시글 작성 검증",
                        () -> assertNotNull((board1), "게시글 생성 확인"),
                        () -> assertEquals(member1, board1.getWriter(), "작성자 객체 불일치"),
                        () -> assertEquals(member1.getName(), board1.getWriter().getName(), "작성자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), board1.getWriter().getNickName(), "작성자 이름 불일치"),
                        () -> assertEquals(member1.getPhone(), board1.getWriter().getPhone(), "작성자 이름 불일치"),
                        () -> assertEquals(member1.getPhone(), board1.getWriter().getPhone(), "작성자 이름 불일치"),
                        () -> assertEquals("title1", board1.getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals("우산 팔아요1", board1.getContent(), "게시글 내용 불일치"),
                        () -> assertEquals("숭실대1", board1.getAddress().getName(), "게시글 주소 이름 불일치"),
                        () -> assertEquals(37.4958, board1.getAddress().getLatitude(), "게시글 주소 위도 불일치"),
                        () -> assertEquals(126.9583, board1.getAddress().getLongitude(), "게시글 주소 경도 불일치"),
                        () -> assertEquals("주소1", board1.getAddress().getStreet(), "게시글 주소 도로명 불일치"),
                        () -> assertEquals(1000L, board1.getPrice(), "게시글 가격 불일치"),
                        () -> assertFalse(board1.isFreebie(), "게시글 나눔여부 불일치")
                ));
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
        boolean updateIsFreebie = true;

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

        Optional.of(updateBoard)
                .filter(board -> board.getWriter().equals(member1))
                .ifPresent(board -> assertAll("게시글 수정 검증",
                        () -> assertNotNull((updateBoard), "게시글 생성 확인"),
                        () -> assertEquals(member1, updateBoard.getWriter(), "작성자 객체 불일치"),
                        () -> assertEquals(member1.getName(), updateBoard.getWriter().getName(), "작성자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), updateBoard.getWriter().getNickName(), "작성자 이름 불일치"),
                        () -> assertEquals(member1.getPhone(), updateBoard.getWriter().getPhone(), "작성자 이름 불일치"),
                        () -> assertEquals(member1.getPhone(), updateBoard.getWriter().getPhone(), "작성자 이름 불일치"),
                        () -> assertEquals(updateTitle, updateBoard.getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals(updateContent, updateBoard.getContent(), "게시글 내용 불일치"),
                        () -> assertEquals(updateFormatStartTime, updateBoard.getStartTimeString(), "게시글 시작 시각(String) 불일치"),
                        () -> assertEquals(updateFormatEndTime, updateBoard.getEndTimeString(), "게시글 종료 시각(String) 불일치"),
                        () -> assertEquals("숭숭숭", updateBoard.getAddress().getName(), "게시글 주소 이름 불일치"),
                        () -> assertEquals(37.5000, updateBoard.getAddress().getLatitude(), "게시글 주소 위도 불일치"),
                        () -> assertEquals(126.9555, updateBoard.getAddress().getLongitude(), "게시글 주소 경도 불일치"),
                        () -> assertEquals("주소2", updateBoard.getAddress().getStreet(), "게시글 주소 도로명 불일치"),
                        () -> assertEquals(updatePrice, updateBoard.getPrice(), "게시글 가격 불일치"),
                        () -> assertTrue(updateBoard.isFreebie(), "게시글 나눔여부 불일치")
                ));
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
