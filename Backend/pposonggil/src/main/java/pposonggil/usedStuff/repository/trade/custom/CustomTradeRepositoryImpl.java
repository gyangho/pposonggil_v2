package pposonggil.usedStuff.repository.trade.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.QMember;
import pposonggil.usedStuff.domain.Trade;

import java.util.List;
import java.util.Optional;

import static pposonggil.usedStuff.domain.QChatRoom.chatRoom;
import static pposonggil.usedStuff.domain.QTrade.trade;

@Repository
public class CustomTradeRepositoryImpl implements CustomTradeRepository {
    private final JPAQueryFactory query;

    public CustomTradeRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    QMember sMember = new QMember("sMember");
    QMember oMember = new QMember("oMember");

    @Override
    public List<Trade> findTradesWithMember() {
        return query
                .select(trade)
                .from(trade)
                .join(trade.tradeSubject, sMember).fetchJoin()
                .join(trade.tradeObject, oMember).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Trade> findTradesBySubjectId(Long subjectId) {
        return query
                .select(trade)
                .from(trade)
                .join(trade.tradeSubject, sMember).fetchJoin()
                .join(trade.tradeObject, oMember).fetchJoin()
                .where(trade.tradeSubject.id.eq(subjectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Trade> findTradesByObjectId(Long objectId) {
        return query
                .select(trade)
                .from(trade)
                .join(trade.tradeSubject, sMember).fetchJoin()
                .join(trade.tradeObject, oMember).fetchJoin()
                .where(trade.tradeObject.id.eq(objectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Trade> findTradesByMemberId(Long memberId) {
        return query
                .select(trade)
                .from(trade)
                .join(trade.tradeSubject, sMember).fetchJoin()
                .join(trade.tradeObject, oMember).fetchJoin()
                .where(trade.tradeSubject.id.eq(memberId)
                        .or(trade.tradeObject.id.eq(memberId)))
                .limit(1000)
                .fetch();
    }

    @Override
    public Optional<Trade> findTradeByChatRoomId(Long chatRoomId) {
        return Optional.ofNullable(query
                .select(trade)
                .from(trade)
                .join(trade.tradeSubject, sMember).fetchJoin()
                .join(trade.tradeObject, oMember).fetchJoin()
                .join(trade.tradeChatRoom, chatRoom).fetchJoin()
                .where(trade.tradeChatRoom.id.eq(chatRoomId))
                .fetchOne());
    }
}
