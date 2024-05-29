package pposonggil.usedStuff.dto.Route.Point;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Route.Point;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PointDto {
    private PointInformationDto pointInformationDto;

    public static PointDto fromEntity(Point point) {
        return PointDto.builder()
                .pointInformationDto(PointInformationDto.fromEntity(point.getPointInfo()))
                .build();
    }

    public static PointDto fromJsonNode(JsonNode node) {
        return PointDto.builder()
                .pointInformationDto(PointInformationDto.fromJsonNode(node))
                .build();
    }
}
