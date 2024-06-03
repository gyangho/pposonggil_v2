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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PathApiController {

    private final PathService pathService;
    private final SubPathService subPathService;

    /**
     * 경로 검색
     * @param startDto: 출발지
     * @param endDto: 목적지
     * @param selectTime: 경로 검색 시각
     * @return
     */
    @PostMapping("/api/paths/by-member/{memberId}")
    public ResponseEntity<Object> createPaths(@RequestPart("startDto") PointInformationDto startDto,
                                              @RequestPart("endDto") PointInformationDto endDto,
                                              @RequestPart("selectTime") String selectTime,
                                              @PathVariable("memberId") Long memberId) {
        try {
            Object response = pathService.createPaths(startDto, endDto, selectTime, memberId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while calling ODsay API", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 경로 저장
     * @param pathDto : 경로 Dto
     * @return 성공 --> "경로를 저장했습니다."
     */
    @PostMapping("/api/path")
    public ResponseEntity<Object> createPath(@RequestBody PathDto pathDto) {
        Long routeId = pathService.createPath(pathDto);

        Map<String, Object> response = new HashMap<>();
        response.put("routeId", routeId);
        response.put("message", "경로를 저장했습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    /**
     * 상세 경로 검색
     * @param pathDto : 경로 Dto
     * @return : default OSRM으로 탐색한 도보경로가 추가된 경로 Dto
     * @throws IOException
     */
    @PostMapping("/api/path/default")
    public ResponseEntity<Object> selectDefaultPath(@RequestBody PathDto pathDto) throws IOException {
        PathDto defaultPathDto = pathService.selectDefaultPath(pathDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "최적 도보 경로가 포함된 상세 경로 입니다.");
        response.put("defaultPathDto", defaultPathDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 뽀송 상세 경로 검색
     * @param pathDto : 경로 Dto
     * @return : pposong OSRM으로 탐색한 도보경로가 추가된 경로 Dto
     * @throws IOException
     */
    @PostMapping("/api/path/pposong")
    public ResponseEntity<Object> selectPposongtPath(@RequestBody PathDto pathDto) throws IOException {
        PathDto defaultPathDto = pathService.selectPposongPath(pathDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "뽀송 도보 경로가 포함된 상세 경로 입니다.");
        response.put("defaultPathDto", defaultPathDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 상세 경로 검색 화면 내 도보경로의 기상 정보 리스트
     * @param pathDto : 경로 Dto
     * @param selectTime : 선택한 시각
     * @return : 기상 정보 리스트
     */
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

    /**
     * 모든 경로 조회
     * @return 모든 경로 Dto 리스트
     */
    @GetMapping("/api/paths")
    public List<PathDto> paths() {
        return pathService.findPaths();
    }

    /**
     * 회원 아이디로 저장된 경로 조회
     * @param memberId : 회원 아이디
     * @return 회원 아이디가 일치하는 경로 Dto 리스트
     */
    @GetMapping("/api/paths/by-member/{memberId}")
    public List<PathDto> pathsByMemberId(@PathVariable Long memberId) {
        return pathService.findPathsByMember(memberId);
    }

    /**
     * 경로 삭제
     * @param pathId: 경로 아이디
     * @return 성공 --> 경로를 삭제하였습니다.
     */
    @DeleteMapping("/api/path/{pathId}")
    public ResponseEntity<String> deletePath(@PathVariable Long pathId) {
        pathService.deletePath(pathId);

        return ResponseEntity.ok("게시글을 삭제하였습니다.");
    }
}