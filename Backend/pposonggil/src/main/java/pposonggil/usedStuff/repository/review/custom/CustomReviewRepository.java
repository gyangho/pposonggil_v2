package pposonggil.usedStuff.repository.review.custom;

import pposonggil.usedStuff.domain.Review;

import java.util.List;
import java.util.Optional;

public interface CustomReviewRepository {
    List<Review> findAllWithMemberTrade();
    List<Review> findReviewsBySubjectId(Long subjectId);
    List<Review> findReviewsByObjectId(Long objectId);
    List<Review> findReviewsByMemberId(Long memberId);
    List<Review> findReviewsByTradeId(Long tradeId);
    Optional<Review> findBySubjectIdAndObjectIdAndTradeId(Long subjectId, Long objectId, Long tradeId);

}
