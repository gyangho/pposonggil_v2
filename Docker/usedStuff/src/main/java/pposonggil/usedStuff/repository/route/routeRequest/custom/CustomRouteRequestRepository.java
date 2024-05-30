package pposonggil.usedStuff.repository.route.routeRequest.custom;

import pposonggil.usedStuff.domain.Route.RouteRequest;

import java.util.List;

public interface CustomRouteRequestRepository {
    List<RouteRequest> findAllWithMember();

    List<RouteRequest> findRouteRequestsByRouteRequesterId(Long routeRequesterId);
}
