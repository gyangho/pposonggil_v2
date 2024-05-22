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
public class Information {
    @Id
    @GeneratedValue
    @Column(name = "information_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trade_id")
    private Trade informationTrade;

    private Long distance;
    private String pposongRoute;
    private Double expectedRain;

    public void setTrade(Trade trade) {
        this.informationTrade = trade;
        trade.getInformations().add(this);
    }

    public static InformationBuilder builder(Trade informationTrade, Long distance, String pposongRoute, Double expectedRain) {
        if(informationTrade == null || distance == null || pposongRoute == null || expectedRain == null){
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        return new InformationBuilder()
                .informationTrade(informationTrade)
                .distance(distance)
                .pposongRoute(pposongRoute)
                .expectedRain(expectedRain);
    }

    public static Information buildInformation(Trade informationTrade, Long distance, String pposongRoute, Double expectedRain) {
        return Information.builder(informationTrade, distance, pposongRoute, expectedRain)
                .build();
    }
}
