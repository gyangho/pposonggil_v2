package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pposonggil.usedStuff.dto.Member.MemberTradeDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Member.MemberTradeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberTradeApiController {
    private final MemberTradeService memberTradeService;
    private final ValidateService validateService;

    /**
     * admin
     * 거래 정보 포함 전체 회원 조회
     * @return 거래 회원 Dto 리스트
     */
    @GetMapping("/api/members/with-trade")
    public List<MemberTradeDto> membersWithTrade() {
        validateService.checkAdminAndThrow();
        return memberTradeService.findMembersWithTrades();
    }

    /**
     * 본인, admin
     * 거래 정보 포함 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 거래 회원 Dto
     */
    @GetMapping("/api/member/with-trade/by-member/{memberId}")
    public MemberTradeDto getMemberWithTrade(@PathVariable Long memberId) {
        validateService.checkAdminMemberIdAndThrow(memberId);
        return memberTradeService.findOneWithTrade(memberId);
    }
}
