package pposonggil.usedStuff.dto.Block;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Block;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class BlockDto {
    private Long blockId;
    private Long subjectId;
    private Long objectId;
    private String subjectNickName;
    private String objectNickName;
    private LocalDateTime createdAt;

    public static BlockDto fromEntity(Block block) {
        return BlockDto.builder()
                .blockId(block.getId())
                .subjectId(block.getBlockSubject().getId())
                .objectId(block.getBlockObject().getId())
                .subjectNickName(block.getBlockSubject().getName())
                .objectNickName(block.getBlockObject().getName())
                .createdAt(block.getCreatedAt())
                .build();
    }
}
