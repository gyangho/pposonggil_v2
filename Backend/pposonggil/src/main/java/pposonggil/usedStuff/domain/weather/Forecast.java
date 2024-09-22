package pposonggil.usedStuff.domain.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@IdClass(ForecastId.class)
public class Forecast implements Serializable {
    @Id
    @Column(name = "DATE", nullable = false, length = 16)
    private String date;

    @Id
    @Column(name = "TIME", nullable = false, length = 8)
    private String time;

    @Id
    @Column(name = "X", nullable = false, length = 8)
    private String x;

    @Id
    @Column(name = "Y", nullable = false, length = 8)
    private String y;

    @Column(name = "RN1", length = 128)
    private String rn1;

    @Column(name = "T1H", length = 8)
    private String t1h;

    @Column(name = "REH", length = 8)
    private String reh;

    @Column(name = "WSD", length = 8)
    private String wsd;

    @Id
    @Column(name = "UPTIME", nullable = false, length = 16)
    private String uptime;

    public static ForecastBuilder builder(String date, String time, String x, String y){
        if (date == null || time == null || x == null || y == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new ForecastBuilder()
                .date(date)
                .time(time)
                .x(x)
                .y(y);
    }

    public static Forecast buildForecast(String date, String time, String x, String y,
                                         String rn1, String t1h, String wsd, String uptime) {
        return Forecast.builder(date, time, x, y)
                .rn1(rn1)
                .t1h(t1h)
                .wsd(wsd)
                .uptime(uptime)
                .build();
    }
}

