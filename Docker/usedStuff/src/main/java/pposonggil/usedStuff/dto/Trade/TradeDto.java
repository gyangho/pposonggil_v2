package pposonggil.usedStuff.dto.Trade;

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
    private Long subjectId;
    private String subjectNickName;
    private Long objectId;
    private String objectNickName;
    private String startTimeString;
    private String endTimeString;
    private TransactionAddress address;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static TradeDto fromEntity(Trade trade) {
        return TradeDto.builder()
                .tradeId(trade.getId())
                .tradeBoardId(trade.getTradeBoard().getId())
                .subjectId(trade.getTradeSubject().getId())
                .subjectNickName(trade.getTradeSubject().getNickName())
                .objectId(trade.getTradeObject().getId())
                .objectNickName(trade.getTradeObject().getNickName())
                .startTimeString(trade.getStartTimeString())
                .endTimeString(trade.getEndTimeString())
                .address(trade.getAddress())
                .createdAt(trade.getCreatedAt())
                .updateAt(trade.getUpdateAt())
                .build();
    }
}
