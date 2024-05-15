package pposonggil.usedStuff.repository.block.custom;

import pposonggil.usedStuff.domain.Block;

import java.util.List;

public interface CustomBlockRepository {
    List<Block> findAllWithMember();
}
