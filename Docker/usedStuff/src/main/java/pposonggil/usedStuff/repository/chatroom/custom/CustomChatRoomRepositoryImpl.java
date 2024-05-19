package pposonggil.usedStuff.repository.chatroom.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.ChatRoom;

import java.util.List;

import static pposonggil.usedStuff.domain.QBoard.board;
import static pposonggil.usedStuff.domain.QChatRoom.chatRoom;
import static pposonggil.usedStuff.domain.QMember.member;

@Repository
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository{
    private final JPAQueryFactory query;
    public CustomChatRoomRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoom> findChatRoomsWithBoardMember(){
        return query
                .select(chatRoom)
                .from(chatRoom)
                .join(chatRoom.chatBoard, board).fetchJoin()
                .join(chatRoom.chatMember, member).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<ChatRoom> findChatRoomsByMemberId(Long memberId){
        return query
                .select(chatRoom)
                .from(chatRoom)
                .where(chatRoom.chatMember.id.eq(memberId)
                        .or(chatRoom.chatBoard.writer.id.eq(memberId)))
                .fetch();
    }
}
