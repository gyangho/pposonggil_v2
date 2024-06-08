package pposonggil.usedStuff.api.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Trade.TradeInformationDto;
import pposonggil.usedStuff.service.Trade.TradeInformationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TradeInformationApiController {
    public final TradeInformationService tradeInformationService;

    /**
     * 정보 포함 거래 상세 조회
     *
     * @param tradeId : 조회하려는 거래 아이디
     * @return 정보 거래 Dto
     */
    @GetMapping("/api/trade/with-information/{tradeId}")
    public TradeInformationDto getTradeWithInformationByTradeId(@PathVariable Long tradeId) {
        return tradeInformationService.findOneWithInformation(tradeId);
    }

    /**
     * 회원 아이디로 정보를 포함한 거래 조회
     *
     * @param memberId : 회원 아이디
     * @return 해당 회원이 참가중인 모든 정보거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-information/by-member/")
    public List<TradeInformationDto> getInformationTradesByMemberId() {
        Long memberId = Long.valueOf(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        return tradeInformationService.findTradesWithInformationByMemberId(memberId);
    }

    /**
     * 회원(게시글 작성자) 아이디가 일치하는 정보를 포함한 거래 조회
     *
     * @param memberId : 회원(게시글 작성자) 아이디
     * @return 게시글 작성자 역할로써 참가중인 정보 거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-information/by-subject/{subjectId}")
    public List<TradeInformationDto> getInformationTradesBySubjectId(@PathVariable Long memberId) {
        return tradeInformationService.findTradesWithInformationBySubjectId(memberId);

    }

    /**
     * 회원(거래 요청자) 아이디가 일치하는 정보 거래 조회
     *
     * @param memberId : 회원(거래 요청자) 아이디
     * @return 거래 요청자 역할로써 참가중인 정보 거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-information/by-object/{objectId}")
    public List<TradeInformationDto> getInformationTradesByObjectId(@PathVariable Long memberId) {
        return tradeInformationService.findTradesWithInformationByObjectId(memberId);

    }

    /**
     * 게시글 & 회원 & 거래 조회
     *
     * @return 게시글, 회원 정보를 포함한 거래 Dto 리스트
     */
    @GetMapping("/api/trades/with-board-member-information")
    public List<TradeInformationDto> getTradesWithBoardMemberInformation() {
        return tradeInformationService.findTradesWithBoardMemberInformation();

    }
}
