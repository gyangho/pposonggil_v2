package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.dto.BlockDto;
import pposonggil.usedStuff.service.BlockService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BlockApiController {
    private final BlockService blockService;

    /**
     * 전체 차단 조회
     */
    @GetMapping("/api/blocks")
    public List<BlockDto> blocks() {
        List<Block> blocks = blockService.findBlocks();

        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 차단 상세 조회
     */
    @GetMapping("/api/block/{blockId}")
    public BlockDto getBlockByBlockId(@PathVariable Long blockId) {
        Block block = blockService.findOne(blockId);
        return BlockDto.fromEntity(block);
    }

    /**
     * 차단자 아이디로 차단 조회
     */
    @GetMapping("/api/blocks/by-subject/{subjectId}")
    public List<BlockDto> getBlocksBySubjectId(@PathVariable Long subjectId) {
        List<Block> blocks = blockService.findBlocksBySubjectId(subjectId);
        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 피차단자 아이디로 차단 조회
     */
    @GetMapping("/api/blocks/by-object/{objectId}")
    public List<BlockDto> getBlocksByObjectId(@PathVariable Long objectId) {
        List<Block> blocks = blockService.findBlocksByObjectId(objectId);
        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 차단 & 차단자 & 피차단자 조회
     */
    @GetMapping("/api/blocks/with-member")
    public List<BlockDto> getBlocksWithMember() {
        List<Block> blocks = blockService.findALlWithMember();
        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 차단 생성
     */
    @PostMapping("/api/block")
    public ResponseEntity<String> createBlock(@RequestBody BlockDto blockDto) {
        Long blockId = blockService.createBlock(blockDto);
        return ResponseEntity.ok("Created block with ID: " + blockId);
    }

    /**
     * 차단 해제
     */
    @DeleteMapping("/api/block/{blockID}")
    public ResponseEntity<String> deleteBlock(@PathVariable Long blockId) {
        blockService.deleteBlock(blockId);

        return ResponseEntity.ok("차단을 해제하였습니다.");
    }

}
