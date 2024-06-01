package pposonggil.usedStuff.dto.Forecast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.weather.Forecast;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ForecastDto {
    private String time;
    private String x;
    private String y;
    private String rn1;
    private String t1h;
    private String reh;
    private String wsd;
    private String uptime;

    public static ForecastDto fromEntity(Forecast forecast){
        return ForecastDto.builder()
                .time(forecast.getTime())
                .x(forecast.getX())
                .y(forecast.getY())
                .rn1(forecast.getRn1())
                .t1h(forecast.getT1h())
                .reh(forecast.getReh())
                .wsd(forecast.getWsd())
                .uptime(forecast.getUptime())
                .build();
    }
}
