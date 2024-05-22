package pposonggil.usedStuff.repository.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.repository.trade.custom.CustomTradeRepository;


public interface TradeRepository extends JpaRepository<Trade, Long>, CustomTradeRepository {
}
