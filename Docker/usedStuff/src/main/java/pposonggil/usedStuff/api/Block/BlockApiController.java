package pposonggil.usedStuff.api.Block;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Block.BlockDto;
import pposonggil.usedStuff.service.Block.BlockService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlockApiController {
    private final BlockService blockService;

    /**
     * 전체 차단 조회
     * @return 차단 Dto 리스트
     */
    @GetMapping("/api/blocks")
    public List<BlockDto> blocks() {
        return blockService.findBlocks();
    }

    /**
     * 특정 차단 상세 조회
     * @param blockId : 조회할 차단 아이디
     * @return 조회한 차단 Dto
     */
    @GetMapping("/api/block/{blockId}")
    public BlockDto getBlockByBlockId(@PathVariable Long blockId) {
        return blockService.findOne(blockId);
    }

    /**
     * 차단자 아이디로 차단 조회
     * @param subjectId : 차단자 아이디
     * @return 차단자 아이디가 일치하는 차단 Dto 리스트
     */
    @GetMapping("/api/blocks/by-subject/{subjectId}")
    public List<BlockDto> getBlocksBySubjectId(@PathVariable Long subjectId) {
        return blockService.findBlocksBySubjectId(subjectId);
    }

    /**
     * 피차단자 아이디로 차단 조회
     * @param objectId : 피차단자 아이디
     * @return 피차단자가 일치하는 차단 Dto 리스트
     */
    @GetMapping("/api/blocks/by-object/{objectId}")
    public List<BlockDto> getBlocksByObjectId(@PathVariable Long objectId) {
        return blockService.findBlocksByObjectId(objectId);
    }

    /**
     * 차단 & 차단자 & 피차단자 조회
     * @return 차단자, 피차단자 정보를 포함한 차단 Dto 리스트
     */
    @GetMapping("/api/blocks/with-member")
    public List<BlockDto> getBlocksWithMember() {
        return blockService.findALlWithMember();
    }

    /**
     * 차단 생성
     * @param blockDto : 차단 Dto
     * @return 성공 -->
     *          "blockId" : [Id]
     *          "message" : "차단을 생성하였습니다."
     */
    @PostMapping("/api/block")
    public ResponseEntity<Object> createBlock(@RequestBody BlockDto blockDto) {
        Long blockId = blockService.createBlock(blockDto);

        Map<String, Object> response = new HashMap<>();
        response.put("blockId", blockId);
        response.put("message", "차단을 생성하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 차단 해제
     * @param blockId : 해제할 차단 아이디
     * @return 성공 --> "차단을 해제하였습니다."
     */
    @DeleteMapping("/api/block/{blockID}")
    public ResponseEntity<String> deleteBlock(@PathVariable Long blockId) {
        blockService.deleteBlock(blockId);

        return ResponseEntity.ok("차단을 해제하였습니다.");
    }

}
