package pposonggil.usedStuff.repository.distance.simplequery;

import lombok.Data;
import pposonggil.usedStuff.domain.Distance;

@Data
public class DistanceSimpleQueryDto {
    private Long distanceId;
    private Long chatRoomId;
    private Long curDistance;

    public DistanceSimpleQueryDto(Distance distance) {
        distanceId = distance.getId();
        chatRoomId = distance.getDistanceChatRoom().getId();
        curDistance = distance.getCurDistance();
    }
}
