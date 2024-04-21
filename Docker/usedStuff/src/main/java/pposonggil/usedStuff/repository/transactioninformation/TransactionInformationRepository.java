package pposonggil.usedStuff.repository.transactioninformation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.TransactionInformation;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionInformationRepository {
    private final EntityManager em;

    public Long save(TransactionInformation transactionInformation) {
        em.persist(transactionInformation);
        return transactionInformation.getId();
    }

    public TransactionInformation findOne(Long id) {
        return em.find(TransactionInformation.class, id);
    }

    public List<TransactionInformation> findAll() {
        return em.createQuery("select t from TransactionInformation t", TransactionInformation.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<TransactionInformation> findAllWithMemberChatRoom() {
        return em.createQuery("select t from TransactionInformation t " +
                "join fetch t.transactionMember m " +
                "join fetch t.transactionChatRoom c",
                TransactionInformation.class)
                .getResultList();
    }

}
