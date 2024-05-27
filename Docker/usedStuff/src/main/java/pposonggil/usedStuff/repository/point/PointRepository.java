package pposonggil.usedStuff.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Point;
import pposonggil.usedStuff.repository.point.custom.CustomPointRepository;

public interface PointRepository extends JpaRepository<Point, Long>, CustomPointRepository {
}
