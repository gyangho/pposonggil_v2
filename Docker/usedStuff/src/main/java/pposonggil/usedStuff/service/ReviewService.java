package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Review;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.ReviewDto;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.review.ReviewRepository;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.util.List;
import java.util.NoSuchElementException;

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
    public List<Review> findReviews() {
        return reviewRepository.findAll();
    }

    /**
     * 리뷰 상세 조회
     */
    public Review findOne(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 리뷰 남긴 사람 아이디로 리뷰 조회
     */
    public List<Review> findReviewsBySubjectId(Long subjectId) {
        return reviewRepository.findReviewsBySubjectId(subjectId);
    }

    /**
     * 리뷰 당한 사람 아이디로 리뷰 조회
     */
    public List<Review> findReviewsByObjectId(Long objectId) {
        return reviewRepository.findReviewsByObjectId(objectId);
    }

    /**
     * 회원 아이디로 연관된 모든 리뷰 조회
     */
    public List<Review> findReviewsByMemberId(Long memberId) {
        return reviewRepository.findReviewsByMemberId(memberId);
    }

    /**
     * 거래 아이디로 리뷰 조회
     */
    public List<Review> findReviewsByTradeId(Long tradeId) {
        return reviewRepository.findReviewsByTradeId(tradeId);
    }

    /**
     * 거래, 리뷰주체, 리뷰 객체 아이디로 리뷰 조회
     */
    public Review findBySubjectIdAndObjectIdAndTradeId(Long subjectId, Long objectId, Long tradeId) {
        return reviewRepository.findBySubjectIdAndObjectIdAndTradeId(subjectId, objectId, tradeId)
                .orElseThrow(() -> new NoSuchElementException("Review not found with subjectId, objectId, tradeId: "
                        + subjectId + ", " + objectId + ", " + tradeId));
    }

    /**
     * 리뷰 남긴 사람 & 리뷰 당한 사람 & 거래 & 리뷰 조회
     */
    public List<Review> findAllWithMemberChatRoom() {
        return reviewRepository.findAllWithMemberTrade();
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
