package pposonggil.usedStuff.repository.block.simplequery;

import lombok.Data;
import pposonggil.usedStuff.domain.Block;

import java.time.LocalDate;

@Data
public class BlockSimpleQueryDto {
    private Long blockId;
    private String subjectNickName;
    private String objectNickName;
    private String blockType;
    private LocalDate createdAt;
    private String content;

    public BlockSimpleQueryDto(Block block) {
        blockId = block.getId();
        subjectNickName = block.getBlockSubject().getNickName();
        objectNickName = block.getBlockObject().getNickName();
        blockType = block.getBlockType();
        createdAt = block.getCreatedAt();
        content = block.getContent();
    }

}
