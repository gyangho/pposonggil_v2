package pposonggil.usedStuff.repository.point.custom;

import pposonggil.usedStuff.domain.Route.Point;

import java.util.List;

public interface CustomPointRepository {
    List<Point> findPointsWithSubPath();

    List<Point> findPointsBySubPathId(Long subPathId);
}
