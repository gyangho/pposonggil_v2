package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

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
public class Path extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "path_id")
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubPath> subPaths  = new ArrayList<>();

    @Embedded
    private PointInformation startInfo;
    @Embedded
    private PointInformation endInfo;
    @Embedded
    private PointInformation midInfo;

    private Long totalDuration;
    private Long price;
    private Long totalWalkDistance;
    private Long totalWalkDuration;

    public static PathBuilder builder(PointInformation startInfo, PointInformation endInfo) {
        if(startInfo == null || endInfo == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new PathBuilder()
                .startInfo(startInfo)
                .endInfo(endInfo);
    }

    public static Path buildPath(PointInformation startInfo, PointInformation endInfo, PointInformation midInfo,
                                 Long totalDuration, Long price, Long totalWalkDistance, Long totalWalkDuration){
        return Path.builder(startInfo, endInfo)
                .midInfo(midInfo)
                .totalDuration(totalDuration)
                .price(price)
                .totalWalkDistance(totalWalkDistance)
                .totalWalkDistance(totalWalkDistance)
                .build();
    }

}
