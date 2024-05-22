package pposonggil.usedStuff.repository.block.custom;

import pposonggil.usedStuff.domain.Block;

import java.util.List;
import java.util.Optional;

public interface CustomBlockRepository {
    List<Block> findAllWithMember();
    List<Block> findBlocksBySubjectId(Long subjectId);
    List<Block> findBlocksByObjectId(Long objectId);
    Optional<Block> findByBlockSubjectAndBlockObject(Long subjectId, Long objectId);

}
