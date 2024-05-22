package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.*;
import pposonggil.usedStuff.dto.BoardDto;
import pposonggil.usedStuff.dto.MemberDto;
import pposonggil.usedStuff.dto.TradeDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class TradeServiceTest {
    @Autowired
    TradeService tradeService;
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2, boardId3;
    private Long tradeId1, tradeId2, tradeId3;

    @BeforeEach
    public void setUp() {
        // 회원 1, 2, 3생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 게시글 1, 2, 3 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);
        boardId3 = createBoard(memberId3, "title3", "우산 팔아요3", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대3", 37.0600, 126.9600, "주소3"), 3000L, false);

        // 거래 1, 2, 3생성
        // 거래 1 : 게시글1(회원1) - 회원3
        // 거래 2 : 게시글2(회원2) - 회원3
        // 거래 3 : 게시글3(회원3) - 회원1
        tradeId1 = createTrade(boardId1, memberId1, memberId3);
        tradeId2 = createTrade(boardId2, memberId2, memberId3);
        tradeId3 = createTrade(boardId3, memberId3, memberId1);
    }

    @Test
    public void 거래_생성() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);

        //then
        Trade trade1 = tradeService.findOne(tradeId1);

        Optional.of(trade1)
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board1))
                .ifPresent(trade -> assertAll("거래 정보 검증",
                        () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                        () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                        () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                        () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                        () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                        () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                        () -> assertEquals("title1", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals("우산 팔아요1", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                        () -> assertEquals("숭실대1", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                        () -> assertEquals(37.4958, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                        () -> assertEquals(126.9583, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                        () -> assertEquals("주소1", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                        () -> assertEquals(1000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                        () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                ));
    }

    @Test
    public void 게시글정보와_회원정보를_포함한_거래_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);
        Board board2 = boardService.findOne(boardId2);
        Board board3 = boardService.findOne(boardId3);

        // then
        List<Trade> trades = tradeService.findTradesWithBoardMember();
        assertEquals(3, trades.size());

        // 첫 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board1))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title1", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 두 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member2) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board2))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래2)",
                            () -> assertEquals("name2", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName2", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01022222222", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title2", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요2", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대2", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.5000, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9500, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소2", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(2000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 세 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member3) &&
                        trade.getTradeObject().equals(member1) &&
                        trade.getTradeBoard().equals(board3))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래3)",
                            () -> assertEquals("name3", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name1", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title3", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요3", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대3", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.0600, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9600, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소3", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(3000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 게시글_작성한_회원의_아이디로_거래_조회() {
        // given
        // 게시글 4 생성(회원1)
        Long boardId4 = createBoard(memberId1, "title4", "우산 팔아요4", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대4", 37.4000, 126.9400, "주소4"), 4000L, false);

        // 거래 4 생성(회원 1 - 회원 2)
        Long trade4 = createTrade(boardId4, memberId1, memberId2);

        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);
        Board board4 = boardService.findOne(boardId4);
        List<Trade> trades = tradeService.findTradesBySubjectId(memberId1);

        // then
        assertEquals(2, trades.size());
        // 첫 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board1))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title1", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 네 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member2) &&
                        trade.getTradeBoard().equals(board4))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name2", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName2", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01022222222", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title4", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요4", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대4", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4000, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9400, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소4", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(4000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 게시글_작성하지_않은_회원의_아이디로_거래_조회() {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);
        Board board2 = boardService.findOne(boardId2);

        // then
        List<Trade> trades = tradeService.findTradesByObjectId(memberId3);
        assertEquals(2, trades.size());
        // 첫 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board1))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title1", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 두 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member2) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board2))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("name2", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName2", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01022222222", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title2", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요2", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대2", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.5000, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9500, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소2", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(2000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 회원_아이디로_참가중인_거래_조회() throws Exception{
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);
        Board board3 = boardService.findOne(boardId3);

        // then
        List<Trade> trades = tradeService.findTradesByMemberId(memberId1);
        assertEquals(2, trades.size());

        // 첫 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board1))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title1", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 세 번째 거래 검증
        trades.stream()
                .filter(trade -> trade.getTradeSubject().equals(member3) &&
                        trade.getTradeObject().equals(member1) &&
                        trade.getTradeBoard().equals(board3))
                .findFirst()
                .ifPresent(trade -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래3)",
                            () -> assertEquals("name3", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name1", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName1", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01011111111", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title3", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요3", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대3", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.0600, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9600, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소3", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                            () -> assertEquals(3000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 게시글_아이디로_거래_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);
        Board board3 = boardService.findOne(boardId3);

        // then
        Trade trade1 = tradeService.findTradeByBoardId(boardId1);
        Optional.of(trade1)
                .filter(trade -> trade.getTradeSubject().equals(member1) &&
                        trade.getTradeObject().equals(member3) &&
                        trade.getTradeBoard().equals(board1))
                .ifPresent(trade -> assertAll("게시글 아이디로 조회한 거래 정보 검증",
                        () -> assertEquals("name1", trade.getTradeSubject().getName(), "게시글 작성자 이름 불일치"),
                        () -> assertEquals("nickName1", trade.getTradeSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                        () -> assertEquals("01011111111", trade.getTradeSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                        () -> assertEquals("name3", trade.getTradeObject().getName(), "거래 요청자 이름 불일치"),
                        () -> assertEquals("nickName3", trade.getTradeObject().getNickName(), "거래 요청자 닉네임 불일치"),
                        () -> assertEquals("01033333333", trade.getTradeObject().getPhone(), "거래 요청자 전화번호 불일치"),
                        () -> assertEquals("title1", trade.getTradeBoard().getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals("우산 팔아요1", trade.getTradeBoard().getContent(), "게시글 내용 불일치"),
                        () -> assertEquals("숭실대1", trade.getTradeBoard().getAddress().getName(), "게시글 장소 이름 불일치"),
                        () -> assertEquals(37.4958, trade.getTradeBoard().getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                        () -> assertEquals(126.9583, trade.getTradeBoard().getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                        () -> assertEquals("주소1", trade.getTradeBoard().getAddress().getStreet(), "게시글 장소 도로명 주소 불일치"),
                        () -> assertEquals(1000L, trade.getTradeBoard().getPrice(), "게시글 가격 불일치"),
                        () -> assertFalse(trade.getTradeBoard().isFreebie(), "게시글 나눔여부 불일치")
                ));
    }


    @Test
    public void 게시글을_작성하지않은_사람과_거래할_수_없다() throws Exception {
        // given
        // 회원 4 생성
        Long memberId4 = createMember("name4", "nickName4", "01044444444");

        // then
        // 거래4(회원4 - 회원1) 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () ->{
            createTrade(boardId1, memberId4, memberId1);
        });
    }

    @Test
    public void 자기_자신과_거래할_수_없다() throws Exception {
        // given
        // 게시글 4
        Long boardId4 = createBoard(memberId1, "title4", "우산 팔아요4", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대4", 37.4000, 126.9400, "주소4"), 4000L, false);

        // then
        // 게시글4에 거래(회원 1 - 회원 1)를 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createTrade(boardId4, memberId1, memberId1);
        });
    }

    @Test
    public void 거래_중복은_불가능하다() throws Exception {
        // then
        // 게시글1에 거래1(회원 1 - 회원 3)가 있으나 게시글1의 거래를 하나 더 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () ->{
            createTrade(boardId1, memberId1, memberId2);
        });
    }

    @Test
    public void 거래_삭제() throws Exception {
        // when
        tradeService.deleteTrade(tradeId1);

        // then
        List<Trade> trades = tradeService.findTrades();
        assertEquals(2, trades.size());
        assertThrows(NoSuchElementException.class, () -> tradeService.findOne(tradeId1));

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
    
    public Long createTrade(Long boardId, Long subjectId, Long objectId) {
        TradeDto tradeDto = TradeDto.builder()
                .tradeBoardId(boardId)
                .tradeSubjectId(subjectId)
                .tradeObjectId(objectId)
                .build();
        return tradeService.createTrade(tradeDto);
    }
}