package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.*;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.dto.Review.ReviewDto;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.Member.MemberService;
import pposonggil.usedStuff.service.Review.ReviewService;
import pposonggil.usedStuff.service.Trade.TradeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ReviewServiceTest {
    @Autowired
    ReviewService reviewService;
    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    TradeService tradeService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2;
    private Long tradeId1, tradeId2;
    private Long reviewId1, reviewId2, reviewId3, reviewId4;

    @BeforeEach
    public void setUp() {
        // 회원 1, 2, 3생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 게시글 1, 2 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);

        // 거래 1, 2 생성
        // 거래 1 : 게시글1(회원1 - 회원3)
        // 거래 2 : 게시글2(회원2 - 회원3)
        tradeId1 = createTrade(boardId1, memberId1, memberId3);
        tradeId2 = createTrade(boardId2, memberId2, memberId3);

        // 리뷰 1, 2, 3, 4 생성
        // 리뷰 1 : 거래1, 회원1 --> 회원3, 5점
        // 리뷰 2 : 거래1, 회원3 --> 회원1, 4점
        // 리뷰 3 : 거래2, 회원2 --> 회원3, 2점
        // 리뷰 4 : 거래2, 회원3 --> 회원2, 0점
        reviewId1 = createReview(memberId1, memberId3, tradeId1, 5L);
        reviewId2 = createReview(memberId3, memberId1, tradeId1, 4L);
        reviewId3 = createReview(memberId2, memberId3, tradeId2, 2L);
        reviewId4 = createReview(memberId3, memberId2, tradeId2, 0L);
    }

    @Test
    public void 리뷰_생성() throws Exception {
        // when
        ReviewDto reviewDto1 = reviewService.findOne(reviewId1);

        // then
        Optional.of(reviewDto1)
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId1) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .ifPresent(reviewDto -> assertAll("리뷰 생성 검증",
                        () -> assertEquals("nickName1", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 닉네임 불일치"),
                        () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals(5L, reviewDto.getScore(), "리뷰 점수 불일치")
                ));
    }

    @Test
    public void 리뷰남긴_사람의_아이디로_모든리뷰_조회() throws Exception {
        // when
        List<ReviewDto> reviewDtos = reviewService.findReviewsBySubjectId(memberId3);

        // then
        assertEquals(2, reviewDtos.size());

        // 리뷰 2 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId3) && reviewDto.getObjectId().equals(memberId1) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("nickName3", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName1", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(4L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 4 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId3) && reviewDto.getObjectId().equals(memberId2) &&
                        reviewDto.getTradeId().equals(tradeId2))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래2)",
                            () -> assertEquals("nickName3", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName2", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(0L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 리뷰당한_사람의_아이디로_모든리뷰_조회() throws Exception {
        // when
        List<ReviewDto> reviewDtos = reviewService.findReviewsByObjectId(memberId3);

        // then
        assertEquals(2, reviewDtos.size());

        // 리뷰 1 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId1) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("nickName1", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(5L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 3 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId2) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId2))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래2)",
                            () -> assertEquals("nickName2", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(2L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 회원_아이디로_연관된_리뷰_조회() throws Exception {
        // when
        List<ReviewDto> reviewDtos = reviewService.findReviewsByMemberId(memberId1);

        // then
        assertEquals(2, reviewDtos.size());

        // 리뷰 1 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId1) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("거래 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("nickName1", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(5L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 2 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId3) && reviewDto.getObjectId().equals(memberId1) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("거래 아이디로 거래 리뷰 검증(거래2)",
                            () -> assertEquals("nickName3", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName1", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(4L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 거래_아이디로_모든리뷰_조회() throws Exception {
        // when

        // then
        List<ReviewDto> reviewDtos = reviewService.findReviewsByTradeId(tradeId1);
        assertEquals(2, reviewDtos.size());

        // 리뷰 1 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId1) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("거래 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("nickName1", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(5L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 2 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId3) && reviewDto.getObjectId().equals(memberId1) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("거래 아이디로 거래 리뷰 검증(거래2)",
                            () -> assertEquals(4L, reviewDto.getScore(), "리뷰 점수 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("nickName1", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치")
                            );
                });
    }

    @Test
    public void 회원정보_거래을_포함한_리뷰_조회() throws Exception {
        // when
        List<ReviewDto> reviewDtos = reviewService.findAllWithMemberChatRoom();

        // then
        assertEquals(4, reviewDtos.size());

        // 첫번째 리뷰 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId1) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 1)",
                            () -> assertEquals("nickName1", reviewDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(5L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 두번째 리뷰 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId3) && reviewDto.getObjectId().equals(memberId1) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 2)",
                            () -> assertEquals("nickName3", reviewDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName1", reviewDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals(4L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 세번째 리뷰 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId2) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId2))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 3)",
                            () -> assertEquals("nickName2", reviewDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals(2L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 네번째 리뷰 검증
        reviewDtos.stream()
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId3) && reviewDto.getObjectId().equals(memberId2) &&
                        reviewDto.getTradeId().equals(tradeId2))
                .findFirst()
                .ifPresent(reviewDto -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 4)",
                            () -> assertEquals("nickName3", reviewDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName2", reviewDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals(0L, reviewDto.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 거래_리뷰주체_리뷰객체_아이디로_거래조회() throws Exception {
        // when
        ReviewDto reviewDto1 = reviewService.findBySubjectIdAndObjectIdAndTradeId(memberId1, memberId3, tradeId1);

        Optional.of(reviewDto1)
                .filter(reviewDto -> reviewDto.getSubjectId().equals(memberId1) && reviewDto.getObjectId().equals(memberId3) &&
                        reviewDto.getTradeId().equals(tradeId1))
                .ifPresent(reviewDto -> assertAll("리뷰 생성 검증",
                        () -> assertEquals("nickName1", reviewDto.getSubjectNickName(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("nickName3", reviewDto.getObjectNickName(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals(5L, reviewDto.getScore(), "리뷰 점수 불일치")
                ));
    }

    @Test
    public void 거래에_없는_사람을_리뷰할_수_없다() throws Exception {
        // then
        // 거래1(회원1 - 회원3)에서 회원 2가 리뷰를 하려고 하는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createReview(memberId2, memberId1, tradeId1, 5L);
        });
    }

    @Test
    public void 자기_자신을_리뷰남길_수_없다() throws Exception {
        // then
        // 거래(회원 1 - 회원 3)에서 회원 1이 회원1을 리뷰하려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createReview(memberId1, memberId1, tradeId1, 5L);
        });
    }

    @Test
    public void 거래리뷰_중복은_불가능하다() throws Exception {
        // then
        // 거래(회원 1 - 회원 3)에 회원1이 회원3을 리뷰를 남겼으나 한번 더 남기려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createReview(memberId1, memberId3, tradeId1, 4L);
        });

    }

    @Test
    public void 리뷰_점수의_범위를_벗어난_점수로_평가할_수_없다() throws Exception {
        // given
        // 게시글 3 생성
        Long boardId3 = createBoard(memberId3, "title3", "우산 팔아요3", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대3", 37.0600, 126.9600, "주소3"), 3000L, false);
        // 거래 3 생성
        Long tradeId3 = createTrade(boardId3, memberId3, memberId1);

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            createReview(memberId3, memberId1, tradeId3, 10L);
        });

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
                .subjectId(subjectId)
                .objectId(objectId)
                .build();
        return tradeService.createTrade(tradeDto);
    }

    public Long createReview(Long subjectId, Long objectId, Long tradeId, Long score) {
        ReviewDto reviewDto = ReviewDto.builder()
                .subjectId(subjectId)
                .objectId(objectId)
                .tradeId(tradeId)
                .score(score)
                .build();

        return reviewService.createReview(reviewDto);
    }
}