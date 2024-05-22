package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.*;
import pposonggil.usedStuff.dto.*;

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
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Trade trade1 = tradeService.findOne(tradeId1);

        // then
        Review review1 = reviewService.findOne(reviewId1);

        Optional.of(review1)
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade1))
                .ifPresent(review -> assertAll("리뷰 생성 검증",
                        () -> assertEquals("name1", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                        () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("name3", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                        () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                        () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                        () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                        () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                        () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                ));
    }

    @Test
    public void 리뷰남긴_사람의_아이디로_모든리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        Trade trade1 = tradeService.findOne(tradeId1);
        Trade trade2 = tradeService.findOne(tradeId2);

        // then
        List<Review> reviews = reviewService.findReviewsBySubjectId(memberId3);
        assertEquals(2, reviews.size());

        // 리뷰 2 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member1) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("name1", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals("01011111111", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(4L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 4 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member2) &&
                        review.getReviewTrade().equals(trade2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래2)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("name2", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                            () -> assertEquals("nickName2", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals("01022222222", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(0L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 리뷰당한_사람의_아이디로_모든리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        Trade trade1 = tradeService.findOne(tradeId1);
        Trade trade2 = tradeService.findOne(tradeId2);

        // then
        List<Review> reviews = reviewService.findReviewsByObjectId(memberId3);
        assertEquals(2, reviews.size());

        // 리뷰 1 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("name1", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 3 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member2) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 리뷰 조회 검증(거래2)",
                            () -> assertEquals("name2", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                            () -> assertEquals("nickName2", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("01022222222", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(2L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 회원_아이디로_연관된_리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Trade trade1 = tradeService.findOne(tradeId1);

        // then
        List<Review> reviews = reviewService.findReviewsByMemberId(memberId1);
        assertEquals(2, reviews.size());

        // 리뷰 1 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("거래 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 2 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member1) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("거래 아이디로 거래 리뷰 검증(거래2)",
                            () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(4L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 거래_아이디로_모든리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Trade trade1 = tradeService.findOne(tradeId1);

        // then
        List<Review> reviews = reviewService.findReviewsByTradeId(tradeId1);
        assertEquals(2, reviews.size());

        // 리뷰 1 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("거래 아이디로 리뷰 조회 검증(거래1)",
                            () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 2 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member1) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("거래 아이디로 거래 리뷰 검증(거래2)",
                            () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(4L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 회원정보_거래을_포함한_리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        Trade trade1 = tradeService.findOne(tradeId1);
        Trade trade2 = tradeService.findOne(tradeId2);

        // then
        List<Review> reviews = reviewService.findAllWithMemberChatRoom();
        assertEquals(4, reviews.size());

        // 첫번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 1)",
                            () -> assertEquals("name1", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(trade1.getStartTimeString(), review.getReviewTrade().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(trade1.getEndTimeString(), review.getReviewTrade().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 두번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member1) &&
                        review.getReviewTrade().equals(trade1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 2)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name1", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01011111111", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(trade1.getStartTimeString(), review.getReviewTrade().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(trade1.getEndTimeString(), review.getReviewTrade().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(4L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 세번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member2) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 3)",
                            () -> assertEquals("name2", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName2", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01022222222", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(trade2.getStartTimeString(), review.getReviewTrade().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(trade2.getEndTimeString(), review.getReviewTrade().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals("숭실대2", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.5000, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9500, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소2", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(2L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 네번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member2) &&
                        review.getReviewTrade().equals(trade2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 거래 정보를 포함한 리뷰 조회 검증 (리뷰 4)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name2", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName2", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01022222222", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(trade2.getStartTimeString(), review.getReviewTrade().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(trade2.getEndTimeString(), review.getReviewTrade().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals("숭실대2", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                            () -> assertEquals(37.5000, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                            () -> assertEquals(126.9500, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                            () -> assertEquals("주소2", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                            () -> assertEquals(0L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 거래_리뷰주체_리뷰객체_아이디로_거래조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Trade trade1 = tradeService.findOne(tradeId1);

        Review review1 = reviewService.findBySubjectIdAndObjectIdAndTradeId(memberId1, memberId3, tradeId1);

        Optional.of(review1)
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewTrade().equals(trade1))
                .ifPresent(review -> assertAll("리뷰 생성 검증",
                        () -> assertEquals("name1", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                        () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("name3", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                        () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals("숭실대1", review.getReviewTrade().getAddress().getName(), "거래 장소 이름 불일치"),
                        () -> assertEquals(37.4958, review.getReviewTrade().getAddress().getLatitude(), "거래 장소 위도 불일치"),
                        () -> assertEquals(126.9583, review.getReviewTrade().getAddress().getLongitude(), "거래  장소 경도 불일치"),
                        () -> assertEquals("주소1", review.getReviewTrade().getAddress().getStreet(), "거래 장소 도로명 주소 불일치"),
                        () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
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
                .tradeSubjectId(subjectId)
                .tradeObjectId(objectId)
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