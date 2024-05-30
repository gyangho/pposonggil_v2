package pposonggil.usedStuff.dto.Route.PointInformation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Route.LatXLngY;
import pposonggil.usedStuff.domain.Route.PointInformation;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PointInformationDto {
    private String name;
    private Double latitude;
    private Double longitude;
    private Long x;
    private Long y;

    public static PointInformationDto fromEntity(PointInformation pointInformation) {
        return PointInformationDto.builder()
                .name(pointInformation.getName())
                .latitude(pointInformation.getLatitude())
                .longitude(pointInformation.getLongitude())
                .x(pointInformation.getX())
                .y(pointInformation.getY())
                .build();
    }

    public PointInformation toEntity() {
        return new PointInformation(this.name, this.latitude, this.longitude, this.x ,this.y);
    }

    public static PointInformationDto fromJsonNode(JsonNode node) {
        Double latitude = Optional.ofNullable(node.get("y")).map(JsonNode::asDouble).orElse(null);
        Double longitude = Optional.ofNullable(node.get("x")).map(JsonNode::asDouble).orElse(null);
        LatXLngY cordinates = null;

        if (latitude != null && longitude != null) {
            cordinates = LatXLngY.convertGRID_GPS(LatXLngY.TO_GRID, latitude, longitude);
        }

        return PointInformationDto.builder()
                .name(Optional.ofNullable(node.get("stationName")).map(JsonNode::asText).orElse(null))
                .latitude(latitude)
                .longitude(longitude)
                .x(cordinates != null ? (long) cordinates.x : null)
                .y(cordinates != null ? (long) cordinates.y : null)
                .build();
    }
}