package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Distance{
    @Id
    @GeneratedValue
    @Column(name = "distance_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trade_id")
    private Trade distanceTrade;

    private Long subjectDistance;
    private Long objectDistance;
    private Double subjectRemainRate;
    private Double objectRemainRate;

    @Embedded
    private TransactionAddress address;

    public void setTrade(Trade trade) {
        this.distanceTrade = trade;
    }

    public static DistanceBuilder builder(Trade distanceTrade) {
        if(distanceTrade == null){
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        return new DistanceBuilder()
                .distanceTrade(distanceTrade);
    }

    public static Distance buildDistance(Trade distanceTrade) {
        return Distance.builder(distanceTrade)
                .address(distanceTrade.getAddress())
                .build();
    }
}
