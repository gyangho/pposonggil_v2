package pposonggil.usedStuff.repository.review.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import pposonggil.usedStuff.domain.*;

import java.util.List;
import java.util.Optional;

import static pposonggil.usedStuff.domain.QReview.review;
import static pposonggil.usedStuff.domain.QTrade.trade;

public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final JPAQueryFactory query;

    public CustomReviewRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    QMember sMember = new QMember("sMember");
    QMember oMember = new QMember("oMember");

    @Override
    public List<Review> findAllWithMemberTrade() {

        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, sMember).fetchJoin()
                .join(review.reviewObject, oMember).fetchJoin()
                .join(review.reviewTrade, trade).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsBySubjectId(Long subjectId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, sMember).fetchJoin()
                .join(review.reviewObject, oMember).fetchJoin()
                .join(review.reviewTrade, trade).fetchJoin()
                .where(review.reviewSubject.id.eq(subjectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsByObjectId(Long objectId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, sMember).fetchJoin()
                .join(review.reviewObject, oMember).fetchJoin()
                .join(review.reviewTrade, trade).fetchJoin()
                .where(review.reviewObject.id.eq(objectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsByMemberId(Long memberId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, sMember).fetchJoin()
                .join(review.reviewObject, oMember).fetchJoin()
                .join(review.reviewTrade, trade).fetchJoin()
                .where(review.reviewSubject.id.eq(memberId)
                        .or(review.reviewObject.id.eq(memberId)))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsByTradeId(Long tradeId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, sMember).fetchJoin()
                .join(review.reviewObject, oMember).fetchJoin()
                .join(review.reviewTrade, trade).fetchJoin()
                .where(review.reviewTrade.id.eq(tradeId))
                .limit(1000)
                .fetch();
    }

    @Override
    public Optional<Review> findBySubjectIdAndObjectIdAndTradeId(Long subjectId, Long objectId, Long tradeId) {
        QMember sMember = new QMember("sMember");
        QMember oMember = new QMember("oMember");

        return Optional.ofNullable(query
                .select(review)
                .from(review)
                .join(review.reviewSubject, sMember).fetchJoin()
                .join(review.reviewObject, oMember).fetchJoin()
                .join(review.reviewTrade, trade).fetchJoin()
                .where(review.reviewSubject.id.eq(subjectId)
                        .and(review.reviewObject.id.eq(objectId)
                                .and(review.reviewTrade.id.eq(tradeId))))
                .fetchOne());
    }
}
