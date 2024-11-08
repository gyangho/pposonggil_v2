package pposonggil.usedStuff.domain.Route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import pposonggil.usedStuff.domain.BaseEntity;

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
public class SubPath extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "subpath_id")
    private Long id;

    @OneToMany(mappedBy = "pointSubPath", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Point> points = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "path_id")
    private Path path;

    private String type;
    private Long distance;
    private Long time;
    private Long stationCount;
    private String subwayName;
    private String busNo;
    private String subwayColor;
    private String busColor;

    @Embedded
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

    public void setPath(Path path) {
        this.path = path;
//        path.getSubPaths().add(this);
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        for (Point point : points) {
            point.setPointSubPath(this);
        }
    }
    public static SubPathBuilder builder() {
        return new SubPathBuilder();
    }

    public static SubPath buildSubPath(Path path, PointInformation start, PointInformation end, String type, Long distance,
                                       Long time, Long stationCount, String subwayName, String busNo, String subwayColor, String busColor) {
        return SubPath.builder()
                .path(path)
                .start(start)
                .end(end)
                .type(type)
                .distance(distance)
                .time(time)
                .stationCount(stationCount)
                .subwayName(subwayName)
                .busNo(busNo)
                .subwayColor(subwayColor)
                .busColor(busColor)
                .build();
    }
}
