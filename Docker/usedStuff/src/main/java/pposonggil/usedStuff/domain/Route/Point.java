package pposonggil.usedStuff.domain.Route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Point {
    @Id
    @GeneratedValue
    @Column(name = "point_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subpath_id")
    private SubPath pointSubPath;

    @Embedded
    private PointInformation pointInfo;

    public void setPointSubPath(SubPath pointSubPath) {
        this.pointSubPath = pointSubPath;
    }

    @Builder
    public Point(PointInformation pointInfo) {
        this.pointInfo = pointInfo;
    }
}
