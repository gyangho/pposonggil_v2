package pposonggil.usedStuff.dto.Forecast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ForecastSubPathDto {
    private String time;
    private String expectedRain;
    private String rn1;
    private String t1h;
    private String reh;
    private String wsd;
    private Double latitude;
    private Double longitude;
}
