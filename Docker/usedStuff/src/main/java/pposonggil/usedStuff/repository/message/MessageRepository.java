package pposonggil.usedStuff.repository.message;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final EntityManager em;

    public void save(MessageRepository messageRepository){
        em.persist(messageRepository);
    }

    public MessageRepository findOne(Long id) {
        return em.find(MessageRepository.class, id);
    }

    public List<MessageRepository> findAll() {
        return em.createQuery("select m from Message m", MessageRepository.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<MessageRepository> findAllWithMemberChatRoom() {
        return em.createQuery("select m from Message m " +
                        "join fetch m.sender s " +
                        "join fetch m.messageChatRoom r", MessageRepository.class)
                .getResultList();
    }
}
