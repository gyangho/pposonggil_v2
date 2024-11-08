package pposonggil.usedStuff.repository.route.subpath;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Route.SubPath;
import pposonggil.usedStuff.repository.route.subpath.custom.CustomSubPathRepository;

public interface SubPathRepository extends JpaRepository<SubPath, Long>, CustomSubPathRepository {
}
