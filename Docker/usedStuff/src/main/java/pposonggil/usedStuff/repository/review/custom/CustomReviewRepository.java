package pposonggil.usedStuff.repository.review.custom;

import pposonggil.usedStuff.domain.Review;

import java.util.List;

public interface CustomReviewRepository {
    List<Review> findAllWithMemberChatRoom();

    List<Review> findReviewsBySubjectId(Long subjectId);

    List<Review> findReviewsByObjectId(Long objectId);

    List<Review> findReviewsByChatRoomId(Long chatRoomId);
}
