package pposonggil.usedStuff.repository.route.routeRequest.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Route.RouteRequest;

import java.util.List;
import java.util.stream.Collectors;

import static pposonggil.usedStuff.domain.QMember.member;
import static pposonggil.usedStuff.domain.Route.QRouteRequest.routeRequest;

@Repository
public class CustomRouteRequestRepositoryImpl implements CustomRouteRequestRepository{
    private final JPAQueryFactory query;

    public CustomRouteRequestRepositoryImpl(EntityManager em){
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<RouteRequest> findAllWithMember(){
        return query
                .select(routeRequest)
                .from(routeRequest)
                .join(routeRequest.routeRequester, member).fetchJoin()
                .limit(1000)
                .fetch();
    }

@Override
    public List<RouteRequest> findRouteRequestsByRouteRequesterId(Long routeRequesterId){
    List<RouteRequest> routeRequests = findAllWithMember();
    return routeRequests.stream()
            .filter(routeRequest -> routeRequest.getRouteRequester().getId().equals(routeRequesterId))
            .collect(Collectors.toList());
    }
}
