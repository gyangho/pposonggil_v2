package pposonggil.usedStuff.repository.route.path.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Route.Path;

import java.util.List;
import java.util.stream.Collectors;

import static pposonggil.usedStuff.domain.QMember.member;
import static pposonggil.usedStuff.domain.Route.QPath.path;

@Repository
public class CustomPathRepositoryImpl implements CustomPathRepository {
    private final JPAQueryFactory query;

    public CustomPathRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Path> findAllWithMember() {
        return query
                .select(path)
                .from(path)
                .join(path.routeRequester, member).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Path> findPathsWithByMemberByRequesterId(Long requesterId) {
        List<Path> paths = findAllWithMember();

        return paths.stream()
                .filter(path -> path.getRouteRequester().getId().equals(requesterId))
                .collect(Collectors.toList());
    }
}
