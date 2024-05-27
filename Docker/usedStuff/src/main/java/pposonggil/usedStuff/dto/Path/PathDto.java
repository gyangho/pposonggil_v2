package pposonggil.usedStuff.dto.Path;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Path;
import pposonggil.usedStuff.domain.PointInformation;
import pposonggil.usedStuff.dto.SubPath.SubPathDto;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PathDto {
    private Long pathId;
    private PointInformation startInfo;
    private PointInformation endInfo;
    private PointInformation midInfo;
    private Long totalDuration;
    private Long price;
    private Long totalWalkDistance;
    private Long totalWalkDuration;
    private List<SubPathDto> subPaths;

    public static PathDto fromEntity(Path path) {
        return PathDto.builder()
                .pathId(path.getId())
                .startInfo(path.getStartInfo())
                .endInfo(path.getEndInfo())
                .midInfo(path.getMidInfo())
                .totalDuration(path.getTotalDuration())
                .price(path.getPrice())
                .totalWalkDistance(path.getTotalWalkDistance())
                .totalWalkDuration(path.getTotalWalkDuration())
                .subPaths(path.getSubPaths().stream()
                        .map(SubPathDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
