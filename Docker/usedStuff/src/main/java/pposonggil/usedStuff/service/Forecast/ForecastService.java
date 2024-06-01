package pposonggil.usedStuff.service.Forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Route.LatXLngY;
import pposonggil.usedStuff.domain.weather.Forecast;
import pposonggil.usedStuff.dto.Forecast.ForecastDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.repository.forecast.ForecastRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForecastService {
    private final ForecastRepository forecastRepository;

    /**
     * 날짜, 시각에 따른 기상 정보 Dto 리스트 조회
     */
    public List<ForecastDto> findForecastsByTime(ForecastDto forecastDto) {
        List<Forecast> forecasts = forecastRepository.findByTime(forecastDto.getTime());
        return forecasts.stream()
                .map(ForecastDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * x, y에 따른 기상 정보 Dto 리스트 조회
     */
    public List<ForecastDto> findForecastsByXAndY(ForecastDto forecastDto){
        List<Forecast> forecasts = forecastRepository.findByXAndY(forecastDto.getX(), forecastDto.getY());
        return forecasts.stream()
                .map(ForecastDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 위도, 경도에 따른 기상 정보 Dto 리스트 조회
     */
    public List<ForecastDto> findForecastsByLatAndLon(PointInformationDto pointInformationDto){
        LatXLngY latXLngY = LatXLngY.convertGRID_GPS(LatXLngY.TO_GRID, pointInformationDto.getLatitude(), pointInformationDto.getLongitude());

        List<Forecast> forecasts = forecastRepository.findByXAndY(String.format("%.0f", latXLngY.x), String.format("%.0f", latXLngY.y));
        return forecasts.stream()
                .map(ForecastDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * 시각, x, y에 따른 기상 정보 Dto
     */
    public ForecastDto findForecastByTimeAndXAndY(ForecastDto forecastDto) {
        Forecast forecast = forecastRepository.findByTimeAndXAndY(forecastDto.getTime(),
                forecastDto.getX(), forecastDto.getY());

        return ForecastDto.fromEntity(forecast);
    }
}
