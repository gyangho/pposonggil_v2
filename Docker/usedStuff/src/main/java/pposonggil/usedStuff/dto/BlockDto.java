package pposonggil.usedStuff.dto;

import lombok.Data;
import pposonggil.usedStuff.domain.Block;

import java.time.LocalDate;

@Data
public class BlockDto {
    private Long blockId;
    private String subjectNickName;
    private String objectNickName;
    private String blockType;
    private LocalDate createdAt;
    private String content;

    public BlockDto(Block block) {
        blockId = block.getId();
        subjectNickName = block.getBlockSubject().getNickName();
        objectNickName = block.getBlockObject().getNickName();
        blockType = block.getBlockType();
        createdAt = block.getCreatedAt();
        content = block.getContent();
    }

}
