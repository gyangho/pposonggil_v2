package pposonggil.usedStuff.repository.route.routeRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Route.RouteRequest;
import pposonggil.usedStuff.repository.route.routeRequest.custom.CustomRouteRequestRepository;

public interface RouteRequestRepository extends JpaRepository<RouteRequest, Long>, CustomRouteRequestRepository {
}
