package pposonggil.usedStuff.dto.Route.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Route.RouteRequest;
import pposonggil.usedStuff.dto.Route.Path.PathDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class RouteRequestDto {
    private Long routeRequestId;
    private Long routeRequesterId;
    private PointInformationDto start;
    private PointInformationDto end;
    private PathDto pathDto;

    public static  RouteRequestDto fromEntity(RouteRequest routeRequest){
        return RouteRequestDto.builder()
                .routeRequestId(routeRequest.getId())
                .routeRequesterId(routeRequest.getRouteRequester().getId())
                .start(PointInformationDto.fromEntity(routeRequest.getStartInfo()))
                .end(PointInformationDto.fromEntity(routeRequest.getEndInfo()))
                .pathDto(PathDto.fromEntity(routeRequest.getRouteRequestPath()))
                .build();
    }
}
