package pposonggil.usedStuff.repository.route.point;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Route.Point;
import pposonggil.usedStuff.repository.route.point.custom.CustomPointRepository;

public interface PointRepository extends JpaRepository<Point, Long>, CustomPointRepository {
}
