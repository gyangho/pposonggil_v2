package pposonggil.usedStuff.repository.Distance;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Distance;
import pposonggil.usedStuff.repository.Distance.custom.CustomDistanceRepository;

public interface DistanceRepository extends JpaRepository<Distance, Long>, CustomDistanceRepository {
}
