package pposonggil.usedStuff.api.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pposonggil.usedStuff.dto.Trade.TradeReviewDto;
import pposonggil.usedStuff.service.Trade.TradeReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TradeReviewApiController {
    public final TradeReviewService tradeReviewService;

    /**
     * 리뷰포함 거래 상세 조회
     *
     * @param tradeId : 조회하려는 거래 아이디
     * @return 리뷰거래 Dto
     */
    @GetMapping("/api/trade/with-review/{tradeId}")
    public TradeReviewDto getTradeWithReviewByTradeId(@PathVariable Long tradeId) {
        return tradeReviewService.findOneWithReview(tradeId);
    }

    /**
     * 회원 아이디로 정보를 포함한 거래 조회
     *
     * @param memberId : 회원 아이디
     * @return 해당 회원이 참가중인 모든 정보거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-review/by-member/{memberId}")
    public List<TradeReviewDto> getReviewTradesByMemberId(@PathVariable Long memberId) {
        return tradeReviewService.findTradesWithReviewByMemberId(memberId);
    }

    /**
     * 회원(게시글 작성자) 아이디가 일치하는 정보를 포함한 거래 조회
     *
     * @param subjectId : 회원(게시글 작성자) 아이디
     * @return 게시글 작성자 역할로써 참가중인 리뷰거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-review/by-subject/{subjectId}")
    public List<TradeReviewDto> getReviewTradesBySubjectId(@PathVariable Long subjectId) {
        return tradeReviewService.findTradesWithReviewBySubjectId(subjectId);

    }

    /**
     * 회원(거래 요청자) 아이디가 일치하는 리뷰거래 조회
     *
     * @param objectId : 회원(거래 요청자) 아이디
     * @return 거래 요청자 역할로써 참가중인 리뷰거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-review/by-object/{objectId}")
    public List<TradeReviewDto> getReviewTradesByObjectId(@PathVariable Long objectId) {
        return tradeReviewService.findTradesWithReviewByObjectId(objectId);

    }

    /**
     * 게시글 & 회원 & 거래 조회
     *
     * @return 게시글, 회원 정보를 포함한 거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-board-member-review")
    public List<TradeReviewDto> findTradesWithBoardMemberReview() {
        return tradeReviewService.findTradesWithBoardMemberReview();
    }
}
