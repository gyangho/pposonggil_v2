package pposonggil.usedStuff.api.Route;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Route.Request.RouteRequestDto;
import pposonggil.usedStuff.service.Route.RouteService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class OdsayApiController {

    private final RouteService routeService;

    @PostMapping("/api/route")
    public ResponseEntity<Object> searchRoutes(@RequestBody RouteRequestDto routeRequestDto) {
        try {
            Object response = routeService.searchPubTransPath(routeRequestDto.getStart(), routeRequestDto.getEnd());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while calling ODsay API", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}