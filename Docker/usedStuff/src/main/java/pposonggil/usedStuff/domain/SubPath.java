package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

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
public class SubPath extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "subpath_id")
    private Long id;

    @OneToMany(mappedBy = "pointSubPath")
    private List<Point> points = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "path_id")
    private Path path;

    private Long type;

    @Embedded
    private Lane lane;

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

    private Long distance;
    private Long duration;

    public void setPath(Path path){
        this.path = path;
        path.getSubPaths().add(this);
    }

    public static SubPathBuilder builder(Path path) {
        if(path == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new SubPathBuilder()
                .path(path);
    }

    public static SubPath buildSubPath(Path path, Long type, Lane lane, PointInformation startInfo, PointInformation endInfo, Long distance, Long duration) {
        return SubPath.builder(path)
                .type(type)
                .startInfo(startInfo)
                .endInfo(endInfo)
                .distance(distance)
                .duration(duration)
                .build();
    }
}
