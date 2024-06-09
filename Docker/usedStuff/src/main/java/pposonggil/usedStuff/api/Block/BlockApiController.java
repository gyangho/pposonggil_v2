package pposonggil.usedStuff.api.Block;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.dto.Block.BlockDto;
import pposonggil.usedStuff.service.Block.BlockService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlockApiController {
    private final BlockService blockService;
    private final ValidateService validateService;

    /**
     * Admin
     * 전체 차단 조회
     * @return 차단 Dto 리스트
     */
    @GetMapping("/api/blocks")
    public List<BlockDto> blocks() {
        validateService.checkAdminAndThrow();
        return blockService.findBlocks();
    }

    /**
     * Admin
     * 특정 차단 상세 조회
     * @param blockId : 조회할 차단 아이디
     * @return 조회한 차단 Dto
     */
    @GetMapping("/api/block/{blockId}")
    public BlockDto getBlockByBlockId(@PathVariable Long blockId) {
        validateService.checkAdminAndThrow();
        return blockService.findOne(blockId);
    }

    /**
     * 본인
     * 차단자 아이디로 차단 조회
     * @param subjectId : 차단자 아이디
     * @return 차단자 아이디가 일치하는 차단 Dto 리스트
     */
    @GetMapping("/api/blocks/by-subject/{subjectId}")
    public List<BlockDto> getBlocksBySubjectId(@PathVariable Long subjectId) {
        validateService.validateMemberIdAndThrow(subjectId);
        return blockService.findBlocksBySubjectId(subjectId);
    }

    /**
     * Admin
     * 피차단자 아이디로 차단 조회
     * @param objectId : 피차단자 아이디
     * @return 피차단자가 일치하는 차단 Dto 리스트
     */
    @GetMapping("/api/blocks/by-object/{objectId}")
    public List<BlockDto> getBlocksByObjectId(@PathVariable Long objectId) {
        validateService.checkAdminAndThrow();
        return blockService.findBlocksByObjectId(objectId);
    }

    /**
     * Admin
     * 차단 & 차단자 & 피차단자 조회
     * @return 차단자, 피차단자 정보를 포함한 차단 Dto 리스트
     */
    @GetMapping("/api/blocks/with-member")
    public List<BlockDto> getBlocksWithMember() {
        validateService.checkAdminAndThrow();
        return blockService.findALlWithMember();
    }

    /**
     * 본인, admin
     * 차단 생성
     * @param blockDto : 차단 Dto
     * @return 성공 -->
     *          "blockId" : [Id]
     *          "message" : "차단을 생성하였습니다."
     */
    @PostMapping("/api/block")
    public ResponseEntity<Object> createBlock(@RequestBody BlockDto blockDto) {
        Long subjectID = blockDto.getSubjectId();
        validateService.validateMemberIdAndThrow(subjectID);

        Long blockId = blockService.createBlock(blockDto);

        Map<String, Object> response = new HashMap<>();
        response.put("blockId", blockId);
        response.put("message", "차단을 생성하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 본인, admin
     * 차단 해제
     * @param blockId : 해제할 차단 아이디
     * @return 성공 --> "차단을 해제하였습니다."
     */
    @DeleteMapping("/api/block/{blockID}")
    public ResponseEntity<String> deleteBlock(@PathVariable Long blockId) {
        BlockDto blockDto =  blockService.findBlocksByBlockID(blockId);
        Long subjectId = blockDto.getSubjectId();
        validateService.validateMemberIdAndThrow(subjectId);

        blockService.deleteBlock(blockId);
        return ResponseEntity.ok("차단을 해제하였습니다.");
    }

}
