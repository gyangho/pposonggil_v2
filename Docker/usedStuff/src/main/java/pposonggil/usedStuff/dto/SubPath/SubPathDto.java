package pposonggil.usedStuff.dto.SubPath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Lane;
import pposonggil.usedStuff.domain.PointInformation;
import pposonggil.usedStuff.domain.SubPath;
import pposonggil.usedStuff.dto.Point.PointDto;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class SubPathDto {
    private Long subPathId;
    private Long pathId;
    private Long type;
    private PointInformation startInfo;
    private PointInformation endInfo;
    private Lane lane;
    private Long distance;
    private Long duration;
    private List<PointDto> points;

    public static SubPathDto fromEntity(SubPath subPath){
        return SubPathDto.builder()
                .subPathId(subPath.getId())
                .pathId(subPath.getPath().getId())
                .type(subPath.getType())
                .startInfo(subPath.getStartInfo())
                .endInfo(subPath.getEndInfo())
                .lane(subPath.getLane())
                .distance(subPath.getDistance())
                .duration(subPath.getDuration())
                .points(subPath.getPoints().stream()
                        .map(PointDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

}
