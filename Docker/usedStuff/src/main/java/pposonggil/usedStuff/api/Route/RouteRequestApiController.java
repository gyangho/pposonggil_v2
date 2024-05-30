package pposonggil.usedStuff.api.Route;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Route.Request.RouteRequestDto;
import pposonggil.usedStuff.service.Route.RouteRequestService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RouteRequestApiController {

    private final RouteRequestService routeRequestService;

    @PostMapping("/api/routes")
    public ResponseEntity<Object> createRoutes(@RequestBody RouteRequestDto routeRequestDto) {
        try {
            Object response = routeRequestService.createRoutes(routeRequestDto.getStart(), routeRequestDto.getEnd());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while calling ODsay API", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/route")
    public ResponseEntity<Object> createRoute(@RequestBody RouteRequestDto routeRequestDto) {
        Long routeId = routeRequestService.createRoute(routeRequestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("routeId", routeId);
        response.put("message", "경로를 저장했습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}