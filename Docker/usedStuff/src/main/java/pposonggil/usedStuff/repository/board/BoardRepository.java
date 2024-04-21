package pposonggil.usedStuff.repository.board;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Board;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final EntityManager em;

    public void save(Board board) {
        em.persist(board);
    }

    public Board findOne(Long id) {
        return em.find(Board.class, id);
    }

    public List<Board> findBoardsByWriterId(Long writerId) {
        return em.createQuery("select b from Board b where b.writer.id = :writerId", Board.class)
                .setParameter("writerId", writerId)
                .getResultList();
    }

    public List<Board> findAll() {
        return em.createQuery("select b from Board b", Board.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Board> findAllWithChatRoomMemberTransactionInformation() {
        return em.createQuery("select b from Board b " +
                        "join fetch b.chatRoom c" +
                        "join fetch b.transactionInformation t " +
                        "join fetch b.writer m", Board.class)
                .getResultList();
    }
}
