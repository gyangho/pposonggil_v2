package pposonggil.usedStuff.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Review;
import pposonggil.usedStuff.repository.review.custom.CustomReviewRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
    Optional<Review> findByReviewSubjectAndReviewObjectAndReviewChatRoom(Member reviewSubject, Member reviewObject, ChatRoom reviewChatRoom);
}
