package pposonggil.usedStuff.repository.review.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import pposonggil.usedStuff.domain.QChatRoom;
import pposonggil.usedStuff.domain.QMember;
import pposonggil.usedStuff.domain.QReview;
import pposonggil.usedStuff.domain.Review;

import java.util.List;

public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final JPAQueryFactory query;

    QReview review = QReview.review;

    QMember subjectMember = new QMember("subjectMember");
    QMember objectMember = new QMember("objectMember");

    QChatRoom chatRoom = new QChatRoom("chatRoom");

    public CustomReviewRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Review> findAllWithMemberChatRoom() {
        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, subjectMember).fetchJoin()
                .join(review.reviewObject, objectMember).fetchJoin()
                .join(review.reviewChatRoom, chatRoom).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsBySubjectId(Long subjectId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewSubject, subjectMember).fetchJoin()
                .where(subjectMember.id.eq(subjectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsByObjectId(Long objectId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewObject, objectMember).fetchJoin()
                .where(objectMember.id.eq(objectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Review> findReviewsByChatRoomId(Long chatRoomId) {
        return query
                .select(review)
                .from(review)
                .join(review.reviewChatRoom, chatRoom).fetchJoin()
                .where(chatRoom.id.eq(chatRoomId))
                .limit(1000)
                .fetch();
    }
}
