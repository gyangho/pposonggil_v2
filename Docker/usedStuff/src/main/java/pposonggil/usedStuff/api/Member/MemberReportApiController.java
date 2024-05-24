package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pposonggil.usedStuff.dto.Member.MemberReportDto;
import pposonggil.usedStuff.service.Member.MemberReportService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberReportApiController {
    private final MemberReportService memberReportService;

    /**
     * 신고 정보 포함 전체 회원 조회
     * @return 신고 회원 Dto 리스트
     */
    @GetMapping("/api/members/with-report")
    public List<MemberReportDto> membersWithReport() {
        return memberReportService.findMembersWithReports();
    }

    /**
     * 신고 정보 포함 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 신고 회원 Dto
     */
    @GetMapping("/api/member/with-report/by-member/{memberId}")
    public MemberReportDto getMemberWithReport(@PathVariable Long memberId) {
        return memberReportService.findOneWithReport(memberId);
    }
}
