package pposonggil.usedStuff.repository.subpath;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.SubPath;
import pposonggil.usedStuff.repository.subpath.custom.CustomSubPathRepository;

public interface SubPathRepository extends JpaRepository<SubPath, Long>, CustomSubPathRepository {
}
