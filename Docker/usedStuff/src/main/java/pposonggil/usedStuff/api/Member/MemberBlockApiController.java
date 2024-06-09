package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pposonggil.usedStuff.dto.Member.MemberBlockDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Member.MemberBlockService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberBlockApiController {
    private final MemberBlockService memberBlockService;
    private final ValidateService validateService;

    /**
     * admin
     * 차단 정보 포함 전체 회원 조회
     * @return 차단 회원 Dto 리스트
     */
    @GetMapping("/api/members/with-block")
    public List<MemberBlockDto> membersWithBlock()
    {
        validateService.checkAdminAndThrow();
        return memberBlockService.findMembersWithBlocks();
    }

    /**
     * 본인, admin
     * 차단 정보 포함 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 차단 회원 Dto
     */
    @GetMapping("/api/member/with-block/by-member/{memberId}")
    public MemberBlockDto getMemberWithBlock(@PathVariable Long memberId) {
        validateService.checkAdminMemberIdAndThrow(memberId);
        return memberBlockService.findOneWithBlock(memberId);
    }
}
