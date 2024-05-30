package pposonggil.usedStuff.api.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.service.Trade.TradeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TradeApiController {
    public final TradeService tradeService;

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
     *          "message" : "거래를 생성하였습니다.."
     */
    @PostMapping("/api/trade")
    public ResponseEntity<Object> createTrade(@RequestBody TradeDto tradeDto) {
        Long tradeId = tradeService.createTrade(tradeDto);

        Map<String, Object> response = new HashMap<>();
        response.put("memberId", tradeId);
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

}
