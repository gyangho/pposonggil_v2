package pposonggil.usedStuff.repository.distance;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Distance;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DistanceRepository {
    private final EntityManager em;

    public void save(Distance distance) {
        em.persist(distance);
    }

    public Distance findOne(Long id) {
        return em.find(Distance.class, id);
    }

    public List<Distance> findAll() {
        return em.createQuery("select d from Distance d", Distance.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Distance> findAllWithChatRoom() {
        return em.createQuery("select d from Distance d " +
                        "join fetch d.distanceChatRoom r", Distance.class)
                .getResultList();
    }
}
