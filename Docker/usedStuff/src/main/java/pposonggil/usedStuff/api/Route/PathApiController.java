package pposonggil.usedStuff.api.Route;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Forecast.ForecastSubPathDto;
import pposonggil.usedStuff.dto.Route.Path.PathDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.service.Route.PathService;
import pposonggil.usedStuff.service.Route.SubPathService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PathApiController {

    private final PathService pathService;
    private final SubPathService subPathService;

    @PostMapping("/api/paths")
    public ResponseEntity<Object> createPaths(@RequestPart("startDto") PointInformationDto startDto,
                                              @RequestPart("endDto") PointInformationDto endDto) {
        try {
            Object response = pathService.createPaths(startDto, endDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while calling ODsay API", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/path")
    public ResponseEntity<Object> createPath(@RequestBody PathDto pathDto) {
        Long routeId = pathService.createPath(pathDto);

        Map<String, Object> response = new HashMap<>();
        response.put("routeId", routeId);
        response.put("message", "경로를 저장했습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/api/path/default")
    public ResponseEntity<Object> selectDefaultPath(@RequestBody PathDto pathDto) throws IOException {
        PathDto defaultPathDto = pathService.selectDefaultPath(pathDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "최적 도보 경로가 포함된 상세 경로 입니다.");
        response.put("defaultPathDto", defaultPathDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/paths/expected-rain")
    public ResponseEntity<Object> selectPathsWithRain(@RequestPart("pathDtos") List<PathDto> pathDtos,
                                                      @RequestPart("selectTime") String selectTime) {
        List<Double> results = new ArrayList<>();

        for (PathDto pathDto : pathDtos) {
            List<ForecastSubPathDto> forecastBySubPath = pathService.createForecastBySubPath(pathDto, selectTime);
            Double result = 0.0;
            for (ForecastSubPathDto forecastSubPathDto : forecastBySubPath) {
                result = result + Double.parseDouble(forecastSubPathDto.getExpectedRain());
            }
            results.add(result);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/path/expected")
    public ResponseEntity<Object> selectPathWithRain(@RequestPart("pathDto") PathDto pathDto,
                                                     @RequestPart("selectTime") String selectTime) {
        List<ForecastSubPathDto> forecastBySubPath = pathService.createForecastBySubPath(pathDto, selectTime);
        Double result = 0.0;

        for (ForecastSubPathDto forecastSubPathDto : forecastBySubPath) {
            result = result + Double.parseDouble(forecastSubPathDto.getExpectedRain());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("forecast", forecastBySubPath);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}