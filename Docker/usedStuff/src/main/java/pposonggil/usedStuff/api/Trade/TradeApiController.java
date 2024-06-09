package pposonggil.usedStuff.api.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Distance.DistanceDto;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.service.Distance.DistanceService;
import pposonggil.usedStuff.service.Trade.TradeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class TradeApiController {
    public final TradeService tradeService;
    public final DistanceService distanceService;

    /**
     * 전체 거래 조회
     * @return 거래 Dto 리스트
     */
    @GetMapping("/api/trades")
    public List<TradeDto> Trades() {
        return tradeService.findTrades();
    }

    /**
     * 거래 상세 조회
     * @param tradeId : 조회하려는 거래 아이디
     * @return 거래 Dto
     */
    @GetMapping("/api/trade/{tradeId}")
    public TradeDto getTradeByTradeId(@PathVariable Long tradeId) {
        return tradeService.findOne(tradeId);
    }

    /**
     * 회원 아이디로 거래 조회
     * @param memberId : 회원 아이디
     * @return 해당 회원이 참가중인 모든 거래 Dto 리스트
     */
    @GetMapping("/api/trades/by-member/{memberId}")
    public List<TradeDto> getTradesByMemberId(@PathVariable Long memberId) {
        return tradeService.findTradesByMemberId(memberId);

    }

    /**
     * 회원(게시글 작성자) 아이디가 일치하는 거래 조회
     * @param memberId : 회원(게시글 작성자) 아이디
     * @return 게시글 작성자 역할로써 참가중인 거래 Dto 리스트
     */
    @GetMapping("/api/trades/by-subject/{subjectId}")
    public List<TradeDto> getTradesBySubjectId(@PathVariable Long memberId) {
        return tradeService.findTradesBySubjectId(memberId);

    }

    /**
     * 회원(거래 요청자) 아이디가 일치하는 거래 조회
     * @param memberId : 회원(거래 요청자) 아이디
     * @return 거래 요청자 역할로써 참가중인 거래 Dto 리스트
     */
    @GetMapping("/api/trades/by-object/{objectId}")
    public List<TradeDto> getTradesByObjectId(@PathVariable Long memberId) {
        return tradeService.findTradesByObjectId(memberId);

    }

    /**
     * 게시글 & 회원 & 거래 조회
     * @return 게시글, 회원 정보를 포함한 거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-board-member")
    public List<TradeDto> getTradesWithBoardMember() {
        return tradeService.findTradesWithBoardMember();

    }

    /**
     * 거래 생성
     * @param tradeDto : 거래 Dto
     * @return 성공 -->
     *          "tradeId" : [Id]
     *          "distanceId" : [Id]
     *          "message" : "거래 및 거리를 생성하였습니다."
     */
    @PostMapping("/api/trade")
    public ResponseEntity<Object> createTrade(@RequestBody TradeDto tradeDto) {
        Long tradeId = tradeService.createTrade(tradeDto);
        DistanceDto distanceDto = DistanceDto.builder()
                .tradeId(tradeId)
                .build();

        Long distanceId = distanceService.createDistance(distanceDto);
        Map<String, Object> response = new HashMap<>();
        response.put("tradeId", tradeId);
        response.put("distanceId", distanceId);
        response.put("message", "거래를 생성하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 거래 삭제
     * @param tradeId : 거래 아이디
     * @return 성공 --> "거래을 삭제하였습니다."
     */
    @DeleteMapping("/api/trade/{tradeId}")
    public ResponseEntity<String> deleteTrade(@PathVariable Long tradeId) {
        tradeService.deleteTrade(tradeId);

        return ResponseEntity.ok("거래을 삭제하였습니다.");
    }

    /**
     * 거래 시작 시각이 지나고
     * 상대방의 거리가 500m 초과 했을 때만
     * 거래 취소 가능
     */
    @DeleteMapping("/api/trade/{tradeId}/by-member/{memberId}")
    public ResponseEntity<String> deleteNewTrade(@PathVariable Long tradeId, @PathVariable Long memberId) throws IllegalAccessException {
        try {
            tradeService.deleteTradeByMember(tradeId, memberId);
            return ResponseEntity.ok("거래를 삭제하였습니다.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 거래 또는 회원을 찾을 수 없습니다.");
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("거래 시작 시간 이후에 취소할 수 있습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
