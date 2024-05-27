package pposonggil.usedStuff.repository.point.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import pposonggil.usedStuff.domain.Point;

import java.util.List;

import static pposonggil.usedStuff.domain.QPoint.point;
import static pposonggil.usedStuff.domain.QSubPath.subPath;

public class CustomPointRepositoryImpl implements CustomPointRepository{
    private final JPAQueryFactory query;

    public CustomPointRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Point> findPointsWithSubPath(){
        return query
                .select(point)
                .from(point)
                .join(point.pointSubPath, subPath).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Point> findPointsBySubPathId(Long subPathId) {
        return query
                .select(point)
                .from(point)
                .join(point.pointSubPath, subPath).fetchJoin()
                .where(point.pointSubPath.id.eq(subPathId))
                .limit(1000)
                .fetch();
    }
}
