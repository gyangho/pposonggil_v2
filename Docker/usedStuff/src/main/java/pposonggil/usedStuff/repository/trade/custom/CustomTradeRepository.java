package pposonggil.usedStuff.repository.trade.custom;

import pposonggil.usedStuff.domain.Trade;

import java.util.List;
import java.util.Optional;

public interface CustomTradeRepository {
    List<Trade> findTradesWithBoardMember();
    List<Trade> findTradesBySubjectId(Long memberId);
    List<Trade> findTradesByObjectId(Long memberId);
    List<Trade> findTradesByMemberId(Long memberId);
    Optional<Trade> findTradeByBoardId(Long boardId);
}
