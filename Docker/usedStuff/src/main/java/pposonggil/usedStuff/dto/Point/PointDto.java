package pposonggil.usedStuff.dto.Point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Point;
import pposonggil.usedStuff.domain.PointInformation;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PointDto {
    private Long pointId;
    private Long subPathId;
    private PointInformation pointInfo;

    public static PointDto fromEntity(Point point) {
        return PointDto.builder()
                .pointId(point.getId())
                .subPathId(point.getPointSubPath().getId())
                .pointInfo(point.getPointInfo())
                .build();
    }
}
