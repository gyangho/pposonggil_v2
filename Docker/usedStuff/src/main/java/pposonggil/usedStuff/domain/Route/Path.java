package pposonggil.usedStuff.domain.Route;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import pposonggil.usedStuff.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Path extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "path_id")
    private Long id;

    private Long totalTime;
    private Long price;
    private Long totalDistance;
    private Long totalWalkDistance;
    private Long totalWalkTime;
    private Long busTransitCount;
    private Long subwayTransitCount;
    private Long totalTransitCount;
    private Long busStationCount;
    private Long subwayStationCount;

    @Builder.Default
    @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubPath> subPaths = new ArrayList<>();

    public static PathBuilder builder() {
        return new PathBuilder();
    }

    public static Path buildPath(Long totalTime, Long price, Long totalDistance, Long totalWalkDistance,
                                 Long totalWalkTime, Long busTransitCount, Long subwayTransitCount,
                                 Long totalTransitCount, Long busStationCount, Long subwayStationCount) {
        return Path.builder()
                .totalTime(totalTime)
                .price(price)
                .totalDistance(totalDistance)
                .totalWalkDistance(totalWalkDistance)
                .totalWalkTime(totalWalkTime)
                .busTransitCount(busTransitCount)
                .subwayTransitCount(subwayTransitCount)
                .totalTransitCount(totalTransitCount)
                .busStationCount(busStationCount)
                .subwayStationCount(subwayStationCount)
                .build();
    }
}
