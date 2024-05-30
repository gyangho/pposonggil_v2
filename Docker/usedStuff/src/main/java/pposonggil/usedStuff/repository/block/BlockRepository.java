package pposonggil.usedStuff.repository.block;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.repository.block.custom.CustomBlockRepository;

public interface BlockRepository extends JpaRepository<Block, Long>, CustomBlockRepository {
}
