package pposonggil.usedStuff.dto;

import lombok.Data;
import pposonggil.usedStuff.domain.Distance;

@Data
public class DistanceDto {
    private Long distanceId;
    private Long chatRoomId;
    private Long curDistance;

    public DistanceDto(Distance distance) {
        distanceId = distance.getId();
        chatRoomId = distance.getDistanceChatRoom().getId();
        curDistance = distance.getCurDistance();
    }
}
