package pposonggil.usedStuff.repository.route.path;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Route.Path;
import pposonggil.usedStuff.repository.route.path.custom.CustomPathRepository;

public interface PathRepository extends JpaRepository<Path, Long>, CustomPathRepository {
}
