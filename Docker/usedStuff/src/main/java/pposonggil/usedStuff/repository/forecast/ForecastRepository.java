package pposonggil.usedStuff.repository.forecast;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.weather.Forecast;
import pposonggil.usedStuff.domain.weather.ForecastId;
import pposonggil.usedStuff.repository.forecast.custom.CustomForecastRepository;

import java.util.List;

public interface ForecastRepository extends JpaRepository<Forecast, ForecastId>, CustomForecastRepository {
    Forecast findByTimeAndXAndY(String time, String x, String y);

    List<Forecast> findByTime(String time);

    List<Forecast> findByXAndY(String x, String y);
}
