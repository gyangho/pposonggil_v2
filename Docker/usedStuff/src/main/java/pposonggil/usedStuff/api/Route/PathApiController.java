package pposonggil.usedStuff.api.Route;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Route.Path.PathDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.service.Route.PathService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PathApiController {

    private final PathService pathService;

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
}