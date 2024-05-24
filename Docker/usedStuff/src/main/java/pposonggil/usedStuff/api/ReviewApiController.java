package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Review.ReviewDto;
import pposonggil.usedStuff.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {
    private final ReviewService reviewService;

    /**
     * 전체 리뷰 조회
     * @return 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews")
    public List<ReviewDto> reviews() {
        return reviewService.findReviews();
    }

    /**
     * 특정 리뷰 상세 조회
     * @param reviewId 조회할 리뷰 아이디
     * @return 조회한 리뷰 Dto 리스트
     */
    @GetMapping("/api/review/{reviewId}")
    public ReviewDto getReviewReviewId(@PathVariable Long reviewId) {
        return reviewService.findOne(reviewId);
    }

    /**
     * 리뷰 남긴 사람의 아이디로 리뷰 조회
     * @param subjectId 조회할 리뷰 남긴 사람의 아이디
     * @return 리뷰 남긴 사람 아이디랑 일치하는 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews/by-subject/{subjectId}")
    public List<ReviewDto> getReviewsBySubjectId(@PathVariable Long subjectId){
        return reviewService.findReviewsBySubjectId(subjectId);
    }

    /**
     * 리뷰 당한 사람의 아이디로 리뷰 조회
     * @param objectId 조회할 리뷰 남김 당한 사람의 아이디
     * @return 리뷰 남김 당한 사람의 아이디랑 일치하는 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews/by-object/{objectId}")
    public List<ReviewDto> getReviewsByObjectId(@PathVariable Long objectId) {
        return reviewService.findReviewsByObjectId(objectId);
    }

    /**
     * 회원 아이디와 연관된 모든 리뷰 조회
     * @param memberId 조회할 회원 아이디
     * @return 회원 아이디가 일치하는 리뷰 리스트
     */
    @GetMapping("/api/reviews/by-member/{memberId}")
    public List<ReviewDto> getReviewByMemberId(@PathVariable Long memberId) {
        return reviewService.findReviewsByMemberId(memberId);
    }

    /**
     * 거래 아이디로 리뷰 조회
     * @param tradeId 조회할 거래 아이디
     * @return 거래 아이디가 일치하는 리뷰 리스트 (2개)
     */
    @GetMapping("/api/reviews/by-trade/{tradeId}")
    public List<ReviewDto> getReviewByChatRoomId(@PathVariable Long tradeId) {
        return reviewService.findReviewsByTradeId(tradeId);
    }

    /**
     * 회원 & 거래 & 리뷰 조회
     * @return 회원, 거래 정보를 포함한 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews/with-member-chatroom")
    public List<ReviewDto> getReviewsWithMemberChatRoom() {
        return reviewService.findAllWithMemberChatRoom();
    }

    /**
     * 리뷰 생성
     * @param reviewDto 생성할 리뷰 Dto
     * @return 생성된 리뷰 Dto
     */
    @PutMapping("/api/reviews")
    public ResponseEntity<String> createReview(@RequestBody ReviewDto reviewDto) {
        Long reviewId = reviewService.createReview(reviewDto);
        return ResponseEntity.ok("Created review with ID : " + reviewId);
    }
}
