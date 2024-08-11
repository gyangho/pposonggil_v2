package pposonggil.usedStuff.domain.weather;

import java.io.Serializable;
import java.util.Objects;

public class ForecastId implements Serializable {
    private String date;
    private String time;
    private String x;
    private String y;
    private String uptime;

    // 기본 생성자
    public ForecastId() {
    }

    public ForecastId(String date, String time, String x, String y, String uptime) {
        this.date = date;
        this.time = time;
        this.x = x;
        this.y = y;
        this.uptime = uptime;
    }

    // getter, setter 생략...

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForecastId)) return false;
        ForecastId that = (ForecastId) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(x, that.x) &&
                Objects.equals(y, that.y) &&
                Objects.equals(uptime, that.uptime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, x, y, uptime);
    }
}
