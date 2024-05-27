package pposonggil.usedStuff.dto.Lane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Lane;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class LaneDto {
    private Long laneId;
    private String name;
    private String color;

    public static LaneDto fromEntity(Lane lane) {
        return LaneDto.builder()
                .laneId(lane.getId())
                .name(lane.getName())
                .color(lane.getColor())
                .build();
    }
}
