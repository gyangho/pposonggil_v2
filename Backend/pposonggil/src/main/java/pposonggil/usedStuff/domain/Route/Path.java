package pposonggil.usedStuff.domain.Route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import pposonggil.usedStuff.domain.BaseEntity;
import pposonggil.usedStuff.domain.Member;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
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
    private Double totalRain;
    private Long busTransitCount;
    private Long subwayTransitCount;
    private Long totalTransitCount;
    private Long busStationCount;
    private Long subwayStationCount;

    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "start_name")),
            @AttributeOverride(name = "latitude", column = @Column(name = "start_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "start_longitude")),
            @AttributeOverride(name = "x", column = @Column(name = "start_x")),
            @AttributeOverride(name = "y", column = @Column(name = "start_y"))
    })
    private PointInformation start;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "end_name")),
            @AttributeOverride(name = "latitude", column = @Column(name = "end_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "end_longitude")),
            @AttributeOverride(name = "x", column = @Column(name = "end_x")),
            @AttributeOverride(name = "y", column = @Column(name = "end_y"))
    })
    private PointInformation end;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "mid_name")),
            @AttributeOverride(name = "latitude", column = @Column(name = "mid_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "mid_longitude")),
            @AttributeOverride(name = "x", column = @Column(name = "mid_x")),
            @AttributeOverride(name = "y", column = @Column(name = "mid_y"))
    })
    private PointInformation mid;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "route_requester_id")
    private Member routeRequester;

    @Builder.Default
    @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubPath> subPaths = new ArrayList<>();

    public void setRouteRequester(Member routeRequester) {
        this.routeRequester = routeRequester;
        routeRequester.getPaths().add(this);
//        if(routeRequest != null && routeRequest.getRouteRequestPath() != this)
//            routeRequest.setRouteRequestPath(this);
    }

    public void setSubPaths(List<SubPath> subPaths) {
        this.subPaths = subPaths;
        for (SubPath subPath : subPaths) {
            subPath.setPath(this);
        }
    }

    public static PathBuilder builder(PointInformation start, PointInformation end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        return new PathBuilder()
                .start(start)
                .end(end);
    }

    public static Path buildPath(Long totalTime, Long price, Long totalDistance, Long totalWalkDistance,
                                 Long totalWalkTime, Long busTransitCount, Long subwayTransitCount,
                                 Long totalTransitCount, Long busStationCount, Long subwayStationCount,
                                 PointInformation start, PointInformation end) {
        return Path.builder(start, end)
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
