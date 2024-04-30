package pposonggil.usedStuff.repository.review;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Review;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    private final EntityManager em;

    public void save(Review review) {
        em.persist(review);
    }

    public Review findOne(Long id){
        return em.find(Review.class, id);
    }

    public List<Review> findAll() {
        return em.createQuery("select r from Review r", Review.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Review> findWithMemberBoard() {
        return em.createQuery("select r from Review r " +
                        "join fetch r.reviewSubject ms " +
                        "join fetch r.reviewObject mo " +
                        "join fetch r.reviewBoard b", Review.class)
                .getResultList();
    }
}
