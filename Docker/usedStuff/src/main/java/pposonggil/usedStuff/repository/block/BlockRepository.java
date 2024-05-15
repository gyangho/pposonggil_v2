package pposonggil.usedStuff.repository.block;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.repository.block.custom.CustomBlockRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long>, CustomBlockRepository {
   Optional<Block> findByBlockSubjectAndBlockObject(Member blockSubject, Member blockObject);
}
