package pposonggil.usedStuff.repository.chatroom.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

import static pposonggil.usedStuff.domain.QChatRoom.chatRoom;
import static pposonggil.usedStuff.domain.QTrade.trade;

@Repository
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository{
    private final JPAQueryFactory query;
    public CustomChatRoomRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoom> findChatRoomsWithTrade() {
        return query
                .select(chatRoom)
                .from(chatRoom)
                .join(chatRoom.chatTrade, trade).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public Optional<ChatRoom> findChatRoomWithTradeByTradeId(Long tradeId){
        List<ChatRoom> chatRooms = findChatRoomsWithTrade();

        return chatRooms.stream()
                .filter(chatRoom -> chatRoom.getChatTrade().getId().equals(tradeId))
                .findFirst();
    }
}
