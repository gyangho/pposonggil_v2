package pposonggil.usedStuff.dto;

import lombok.*;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class TradeDto {
    private Long tradeId;
    private Long tradeBoardId;
    private Long tradeSubjectId;
    private String tradeSubjectNickName;
    private Long tradeObjectId;
    private String tradeObjectNickName;
    private String startTimeString;
    private String endTimeString;
    private TransactionAddress address;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static TradeDto fromEntity(Trade trade) {
        return TradeDto.builder()
                .tradeId(trade.getId())
                .tradeBoardId(trade.getTradeBoard().getId())
                .tradeSubjectId(trade.getTradeSubject().getId())
                .tradeSubjectNickName(trade.getTradeSubject().getNickName())
                .tradeObjectId(trade.getTradeObject().getId())
                .tradeObjectNickName(trade.getTradeObject().getNickName())
                .startTimeString(trade.getStartTimeString())
                .endTimeString(trade.getEndTimeString())
                .address(trade.getAddress())
                .createdAt(trade.getCreatedAt())
                .updateAt(trade.getUpdateAt())
                .build();
    }
}
