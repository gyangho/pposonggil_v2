package pposonggil.usedStuff.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Review;
import pposonggil.usedStuff.repository.review.custom.CustomReviewRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
}
