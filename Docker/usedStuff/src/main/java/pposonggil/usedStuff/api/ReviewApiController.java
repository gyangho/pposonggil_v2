package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Review;
import pposonggil.usedStuff.dto.ReviewDto;
import pposonggil.usedStuff.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

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
        List<Review> reviews = reviewService.findReviews();

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 리뷰 상세 조회
     * @param reviewId 조회할 리뷰 아이디
     * @return 조회한 리뷰 Dto 리스트
     */
    @GetMapping("/api/review/{reviewId}")
    public ReviewDto getReviewReviewId(@PathVariable Long reviewId) {
        Review review = reviewService.findOne(reviewId);
        return ReviewDto.fromEntity(review);
    }

    /**
     * 리뷰 남긴 사람의 아이디로 리뷰 조회
     * @param subjectId 조회할 리뷰 남긴 사람의 아이디
     * @return 리뷰 남긴 사람 아이디랑 일치하는 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews/by-subject/{subjectId}")
    public List<ReviewDto> getReviewsBySubjectId(@PathVariable Long subjectId){
        List<Review> reviews = reviewService.findReviewsBySubjectId(subjectId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 당한 사람의 아이디로 리뷰 조회
     * @param objectId 조회할 리뷰 남김 당한 사람의 아이디
     * @return 리뷰 남김 당한 사람의 아이디랑 일치하는 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews/by-object/{objectId}")
    public List<ReviewDto> getReviewsByObjectId(@PathVariable Long objectId) {
        List<Review> reviews = reviewService.findReviewsByObjectId(objectId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 아이디로 리뷰 조회
     * @param chatRoomId 조회할 채팅방 아이디
     * @return 채팅방 아이디가 일치하는 리뷰 리스트 (2개)
     */
    @GetMapping("/api/reviews/by-chatRoomId/{chatRoomId}")
    public List<ReviewDto> getReviewByChatRoomId(@PathVariable Long chatRoomId) {
        List<Review> reviews = reviewService.findReviewsByChatRoomId(chatRoomId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 & 회원 & 채팅방 조회
     * @return 회원, 채팅방 정보를 포함한 리뷰 Dto 리스트
     */
    @GetMapping("/api/reviews/with-member-chatroom")
    public List<ReviewDto> getReviewsWithMemberChatRoom() {
        List<Review> reviews = reviewService.findAllWithMemberChatRoom();

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
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
