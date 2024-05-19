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
    ChatRoomService chatRoomService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2;
    private Long chatRoomId1, chatRoomId2;
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

        // 채팅방 1, 2 생성
        // 채팅방 1 : 게시글1(회원1) - 회원3
        // 채팅방 2 : 게시글2(회원2) - 회원3
        chatRoomId1 = createChatRoom(boardId1, memberId3);
        chatRoomId2 = createChatRoom(boardId2, memberId3);

        // 리뷰 1, 2, 3, 4 생성
        // 리뷰 1 : 채팅방1, 회원1 --> 회원3, 5점
        // 리뷰 2 : 채팅방1, 회원3 --> 회원1, 4점
        // 리뷰 3 : 채팅방2, 회원2 --> 회원3, 2점
        // 리뷰 4 : 채팅방2, 회원3 --> 회원2, 0점
        reviewId1 = createReview(memberId1, memberId3, chatRoomId1, 5L);
        reviewId2 = createReview(memberId3, memberId1, chatRoomId1, 4L);
        reviewId3 = createReview(memberId2, memberId3, chatRoomId2, 2L);
        reviewId4 = createReview(memberId3, memberId2, chatRoomId2, 0L);
    }

    @Test
    public void 리뷰_생성() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);

        // then
        Review review1 = reviewService.findOne(reviewId1);

        Optional.of(review1)
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .ifPresent(review -> assertAll("리뷰 생성 검증",
                        () -> assertEquals("name1", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                        () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                        () -> assertEquals("name3", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                        () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                        () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                ));
    }

    @Test
    public void 리뷰남긴_사람의_아이디로_모든리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);

        // then
        List<Review> reviews = reviewService.findReviewsBySubjectId(memberId3);
        assertEquals(2, reviews.size());

        // 리뷰 2 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member1) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 채팅방 조회 검증(채팅방1)",
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
                        review.getReviewChatRoom().equals(chatRoom2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 채팅방 조회 검증(채팅방2)",
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

        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);

        // then
        List<Review> reviews = reviewService.findReviewsByObjectId(memberId3);
        assertEquals(2, reviews.size());

        // 리뷰 1 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 채팅방 조회 검증(채팅방1)",
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
                        review.getReviewChatRoom().equals(chatRoom2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 채팅방 조회 검증(채팅방2)",
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
    public void 채팅방_아이디로_모든리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);

        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);

        // then
        List<Review> reviews = reviewService.findReviewsByChatRoomId(chatRoomId1);
        assertEquals(2, reviews.size());

        // 리뷰 1 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) && review.getReviewObject().equals(member3) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 채팅방 조회 검증(채팅방1)",
                            () -> assertEquals("name1", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 리뷰 2 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) && review.getReviewObject().equals(member1) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("리뷰 남긴 회원의 아이디로 채팅방 조회 검증(채팅방1)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "리뷰 남긴 회원의 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "리뷰 남긴 회원의 불일치"),
                            () -> assertEquals("name1", review.getReviewObject().getName(), "리뷰 당한 회원의 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewObject().getNickName(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals("01011111111", review.getReviewObject().getPhone(), "리뷰 당한 회원의 불일치"),
                            () -> assertEquals(4L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }

    @Test
    public void 회원정보_채팅방을_포함한_리뷰_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);

        // then
        List<Review> reviews = reviewService.findAllWithMemberChatRoom();
        assertEquals(4, reviews.size());

        // 첫번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member1) &&  review.getReviewObject().equals(member3) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 채팅방 정보를 포함한 리뷰 조회 검증 (리뷰 1)",
                            () -> assertEquals("name1", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(chatRoom1.getStartTimeString(), review.getReviewChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(chatRoom1.getEndTimeString(), review.getReviewChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals(5L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 두번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) &&  review.getReviewObject().equals(member1) &&
                        review.getReviewChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 채팅방 정보를 포함한 리뷰 조회 검증 (리뷰 2)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name1", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName1", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01011111111", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(chatRoom1.getStartTimeString(), review.getReviewChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(chatRoom1.getEndTimeString(), review.getReviewChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals(4L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 세번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member2) &&  review.getReviewObject().equals(member3) &&
                        review.getReviewChatRoom().equals(chatRoom2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 채팅방 정보를 포함한 리뷰 조회 검증 (리뷰 3)",
                            () -> assertEquals("name2", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName2", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01022222222", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(chatRoom2.getStartTimeString(), review.getReviewChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(chatRoom2.getEndTimeString(), review.getReviewChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals(2L, review.getScore(), "리뷰 점수 불일치")
                    );
                });

        // 네번째 리뷰 검증
        reviews.stream()
                .filter(review -> review.getReviewSubject().equals(member3) &&  review.getReviewObject().equals(member2) &&
                        review.getReviewChatRoom().equals(chatRoom2))
                .findFirst()
                .ifPresent(review -> {
                    assertAll("회원 정보, 채팅방 정보를 포함한 리뷰 조회 검증 (리뷰 4)",
                            () -> assertEquals("name3", review.getReviewSubject().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", review.getReviewSubject().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", review.getReviewSubject().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name2", review.getReviewObject().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName2", review.getReviewObject().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01022222222", review.getReviewObject().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals(chatRoom2.getStartTimeString(), review.getReviewChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                            () -> assertEquals(chatRoom2.getEndTimeString(), review.getReviewChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                            () -> assertEquals(0L, review.getScore(), "리뷰 점수 불일치")
                    );
                });
    }


    @Test
    public void 채팅방에_없는_사람을_리뷰할_수_없다() throws Exception {
        // then
        // 채팅방1(회원1 - 회원3)에서 회원 2가 리뷰를 하려고 하는 상황
        assertThrows(IllegalArgumentException.class, () ->{
            createReview(memberId2, memberId1, chatRoomId1, 5L);
        });
    }

    @Test
    public void 자기_자신을_리뷰남길_수_없다() throws Exception {
        // then
        // 채팅방(회원 1 - 회원 3)에서 회원 1이 회원1을 리뷰하려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createReview(memberId1, memberId1, chatRoomId1, 5L);
        });
    }

    @Test
    public void 거래리뷰_중복은_불가능하다() throws Exception {
        // then
        // 채팅방(회원 1 - 회원 3)에 회원1이 회원3을 리뷰를 남겼으나 한번 더 남기려는 상황
        assertThrows(IllegalArgumentException.class, () ->{
            createReview(memberId1, memberId3, chatRoomId1, 4L);
        });

    }

    @Test
    public void 리뷰_점수의_범위를_벗어난_점수로_평가할_수_없다() throws Exception {
        // given
        // 게시글 3 생성
        Long boardId3 = createBoard(memberId3, "title3", "우산 팔아요3", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대3", 37.0600, 126.9600, "주소3"), 3000L, false);
        // 채팅방 3 생성
        Long chatRoomId3 = createChatRoom(boardId3, memberId1);

        // when

        // then
        assertThrows(IllegalArgumentException.class, () ->{
            createReview(memberId3, memberId1, chatRoomId3, 10L);
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

    public Long createChatRoom(Long boardId, Long memberId) {
        Member member = memberService.findOne(memberId);
        Board board = boardService.findOne(boardId);

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .chatBoardId(boardId)
                .writerId(board.getWriter().getId())
                .writerNickName(board.getWriter().getNickName())
                .chatMemberId(memberId)
                .chatMemberNickName(member.getNickName())
                .startTimeString(board.getStartTimeString())
                .endTimeString(board.getEndTimeString())
                .address(board.getAddress())
                .build();

        return chatRoomService.createChatRoom(chatRoomDto);
    }

    public Long createReview(Long subjectId, Long objectId, Long chatRoomId, Long score) {
        ReviewDto reviewDto = ReviewDto.builder()
                .subjectId(subjectId)
                .objectId(objectId)
                .chatRoomId(chatRoomId)
                .score(score)
                .build();

        return reviewService.createReview(reviewDto);
    }
}