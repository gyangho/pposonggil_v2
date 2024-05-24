package pposonggil.usedStuff.repository.message.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Message;

import java.util.List;

import static pposonggil.usedStuff.domain.QChatRoom.chatRoom;
import static pposonggil.usedStuff.domain.QMember.member;
import static pposonggil.usedStuff.domain.QMessage.message;

@Repository
public class CustomMessageRepositoryImpl implements  CustomMessageRepository{
    private final JPAQueryFactory query;

    public CustomMessageRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Message> findAllWithMemberChatRoom(){
        return query
                .select(message)
                .from(message)
                .join(message.sender, member).fetchJoin()
                .join(message.messageChatRoom, chatRoom).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Message> findMessagesByChatRoomId(Long chatRoomId){
        return query
                .select(message)
                .from(message)
                .join(message.sender, member).fetchJoin()
                .join(message.messageChatRoom, chatRoom).fetchJoin()
                .where(message.messageChatRoom.id.eq(chatRoomId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Message> findMessagesBySenderId(Long senderId){
        return query
                .select(message)
                .from(message)
                .join(message.sender, member).fetchJoin()
                .join(message.messageChatRoom, chatRoom).fetchJoin()
                .where(message.sender.id.eq(senderId))
                .limit(1000)
                .fetch();
    }
}
