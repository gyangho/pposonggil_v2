package pposonggil.usedStuff.dto.Distance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Distance;
import pposonggil.usedStuff.dto.TransactionAddres.TransactionAddressDto;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class DistanceDto {
    private Long distanceId;
    private Long tradeId;
    private Long subjectId;
    private Long objectId;
    private String subjectName;
    private String objectName;
    private Long subjectDistance;
    private Long objectDistance;
    private Double subjectRemainRate;
    private Double objectRemainRate;
    private TransactionAddressDto transactionAddressDto;

    public static DistanceDto fromEntity(Distance distance) {
        return DistanceDto.builder()
                .distanceId(distance.getId())
                .tradeId(distance.getDistanceTrade().getId())
                .subjectId(distance.getDistanceTrade().getTradeSubject().getId())
                .objectId(distance.getDistanceTrade().getTradeObject().getId())
                .subjectName(distance.getDistanceTrade().getTradeSubject().getName())
                .objectName(distance.getDistanceTrade().getTradeObject().getName())
                .transactionAddressDto(TransactionAddressDto.fromEntity(distance.getAddress()))
                .build();
    }
}
