package pposonggil.usedStuff.dto.Information;

import lombok.*;
import pposonggil.usedStuff.domain.Information;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class InformationDto {
    private Long informationId;
    private Long tradeId;
    private Long distance;
    private String pposongRoute;
    private Double expectedRain;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static InformationDto fromEntity(Information information) {
        return InformationDto.builder()
                .informationId(information.getId())
                .tradeId(information.getInformationTrade().getId())
                .distance(information.getDistance())
                .pposongRoute(information.getPposongRoute())
                .expectedRain(information.getExpectedRain())
                .createdAt(information.getCreatedAt())
                .updateAt(information.getUpdateAt())
                .build();
    }
}
