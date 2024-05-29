package pposonggil.usedStuff.repository.subpath.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Route.SubPath;

import java.util.List;

import static pposonggil.usedStuff.domain.Route.QPath.path;
import static pposonggil.usedStuff.domain.Route.QSubPath.subPath;

@Repository
public class CustomSubPathRepositoryImpl implements CustomSubPathRepository {
    private final JPAQueryFactory query;

    public CustomSubPathRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<SubPath> findSubPathsWithPath() {
        return query.select(subPath)
                .from(subPath)
                .join(subPath.path, path).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<SubPath> findSubPathsByPathId(Long pathId) {
        return query.select(subPath)
                .from(subPath)
                .join(subPath.path, path).fetchJoin()
                .where(subPath.path.id.eq(pathId))
                .limit(1000)
                .fetch();
    }
}
