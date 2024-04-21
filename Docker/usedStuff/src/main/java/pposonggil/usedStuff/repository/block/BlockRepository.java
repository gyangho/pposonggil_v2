package pposonggil.usedStuff.repository.block;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Block;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockRepository {
    private final EntityManager em;

    public void save(Block block) {
        em.persist(block);
    }

    public Block findOne(Long id) {
        return em.find(Block.class, id);
    }

    public List<Block> findAll() {
        return em.createQuery("select b from Block b", Block.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Block> findAllWithMember() {
        return em.createQuery("select b from Board b " +
                        "join fetch b.blockSubject ms " +
                        "join fetch b.blockObject mo", Block.class)
                .getResultList();
    }

}
