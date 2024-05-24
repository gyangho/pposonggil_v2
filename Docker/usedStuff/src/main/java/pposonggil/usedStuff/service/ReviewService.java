package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Review;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.Review.ReviewDto;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.review.ReviewRepository;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final TradeRepository tradeRepository;

    /**
     * 전체 리뷰 조회
     */
    public List<ReviewDto> findReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 상세 조회
     */
    public ReviewDto findOne(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(NoSuchElementException::new);

        return ReviewDto.fromEntity(review);
    }

    /**
     * 리뷰 남긴 사람 아이디로 리뷰 조회
     */
    public List<ReviewDto> findReviewsBySubjectId(Long subjectId) {
        List<Review> reviews = reviewRepository.findReviewsBySubjectId(subjectId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 당한 사람 아이디로 리뷰 조회
     */
    public List<ReviewDto> findReviewsByObjectId(Long objectId) {
        List<Review> reviews = reviewRepository.findReviewsByObjectId(objectId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 연관된 모든 리뷰 조회
     */
    public List<ReviewDto> findReviewsByMemberId(Long memberId) {
        List<Review> reviews = reviewRepository.findReviewsByMemberId(memberId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 거래 아이디로 리뷰 조회
     */
    public List<ReviewDto> findReviewsByTradeId(Long tradeId) {
        List<Review> reviews = reviewRepository.findReviewsByTradeId(tradeId);

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 거래, 리뷰주체, 리뷰 객체 아이디로 리뷰 조회
     */
    public ReviewDto findBySubjectIdAndObjectIdAndTradeId(Long subjectId, Long objectId, Long tradeId) {
        Review review = reviewRepository.findBySubjectIdAndObjectIdAndTradeId(subjectId, objectId, tradeId)
                .orElseThrow(() -> new NoSuchElementException("Review not found with subjectId, objectId, tradeId: "
                        + subjectId + ", " + objectId + ", " + tradeId));

        return ReviewDto.fromEntity(review);
    }

    /**
     * 리뷰 남긴 사람 & 리뷰 당한 사람 & 거래 & 리뷰 조회
     */
    public List<ReviewDto> findAllWithMemberChatRoom() {
        List<Review> reviews = reviewRepository.findAllWithMemberTrade();

        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 생성
     */
    @Transactional
    public Long createReview(ReviewDto reviewDto) {
        Member reviewSubject = memberRepository.findById(reviewDto.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reviewDto.getSubjectId()));
        Member reviewObject = memberRepository.findById(reviewDto.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reviewDto.getObjectId()));
        Trade reviewTrade = tradeRepository.findById(reviewDto.getTradeId())
                .orElseThrow(() -> new NoSuchElementException("Trade not found with id: " + reviewDto.getTradeId()));

        if ((reviewTrade.getTradeSubject() != reviewSubject && reviewTrade.getTradeSubject() != reviewObject) ||
                (reviewTrade.getTradeObject() != reviewObject && reviewTrade.getTradeObject() != reviewSubject)) {
            throw new IllegalArgumentException("거래에 포함되지 않은 사용자를 리뷰할 수 없습니다.");
        }

        if (reviewSubject.equals(reviewObject)) {
            throw new IllegalArgumentException("자기 자신을 리뷰할 수는 없습니다.");
        }

        reviewRepository.findBySubjectIdAndObjectIdAndTradeId(reviewSubject.getId(), reviewObject.getId(), reviewTrade.getId())
                .ifPresent(block -> {
                    throw new IllegalArgumentException("해당 사용자과의 거래를 이전에 리뷰했습니다.");
                });

        Review review = Review.buildReview(reviewSubject, reviewObject, reviewDto.getScore(), reviewDto.getContent());

        review.setReviewSubject(reviewSubject);
        review.setReviewObject(reviewObject);
        review.setReviewTrade(reviewTrade);
        reviewRepository.save(review);

        return review.getId();
    }
}
