package pposonggil.usedStuff.domain.Route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import pposonggil.usedStuff.domain.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@ToString
public class RouteRequest extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "route_request_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "route_request_path_id")
    private Path routeRequestPath;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "route_requester_id")
    private Member routeRequester;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "start_name")),
            @AttributeOverride(name = "latitude", column = @Column(name = "start_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "start_longitude")),
            @AttributeOverride(name = "x", column = @Column(name = "start_x")),
            @AttributeOverride(name = "y", column = @Column(name = "start_y"))
    })
    private PointInformation startInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "end_name")),
            @AttributeOverride(name = "latitude", column = @Column(name = "end_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "end_longitude")),
            @AttributeOverride(name = "x", column = @Column(name = "end_x")),
            @AttributeOverride(name = "y", column = @Column(name = "end_y"))
    })
    private PointInformation endInfo;

    public void setRouteRequester(Member member) {
        this.routeRequester = member;
        member.getRouteRequests().add(this);
    }

    public static RouteRequest.RouteRequestBuilder builder(Member routeRequester, Path path,
                                                           PointInformation startInfo, PointInformation endInfo) {
        if (routeRequester == null || path == null || startInfo == null || endInfo == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new RouteRequestBuilder()
                .routeRequester(routeRequester)
                .routeRequestPath(path)
                .startInfo(startInfo)
                .endInfo(endInfo);
    }

    public static RouteRequest buildRouteRequest(Member routeRequester, Path path,
                                                 PointInformation startInfo, PointInformation endInfo) {
        return RouteRequest.builder(routeRequester, path, startInfo, endInfo)
                .build();
    }

}
