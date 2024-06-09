package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pposonggil.usedStuff.dto.Member.MemberMessageDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Member.MemberMessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberMessageApiController {
    private final MemberMessageService memberMessageService;
    private final ValidateService validateService;

    /**
     * admin
     * 메시지 정보 포함 전체 회원 조회
     * @return 메시지 회원 Dto 리스트
     */
    @GetMapping("/api/members/with-message")
    public List<MemberMessageDto> membersWithMessages() {
        validateService.checkAdminAndThrow();
        return memberMessageService.findMembersWithMessages();
    }

    /**
     * 본인, admin
     * 메시지 정보 포함 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 메시지 회원 Dto
     */
    @GetMapping("/api/member/with-message/by-member/{memberId}")
    public MemberMessageDto getMemberWithMessage(@PathVariable Long memberId) {
        validateService.checkAdminMemberIdAndThrow(memberId);
        return memberMessageService.findOneWithMessage(memberId);
    }
}
