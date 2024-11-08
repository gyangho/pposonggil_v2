package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pposonggil.usedStuff.dto.Member.MemberReviewDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Member.MemberReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberReviewApiController {
    private final MemberReviewService memberReviewService;
    private final ValidateService validateService;

    /**
     * 리뷰 정보 포함 전체 회원 조회
     * @return 리뷰 회원 Dto 리스트
     */
    @GetMapping("/api/members/with-review")
    public List<MemberReviewDto> membersWithReview() {
        validateService.checkAdminAndThrow();
        return memberReviewService.findMembersWithReviews();
    }

    /**
     * 본인, admin
     * 리뷰 정보 포함 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 리뷰 회원 Dto
     */
    @GetMapping("/api/member/with-review/by-member/{memberId}")
    public MemberReviewDto getMemberWithReview(@PathVariable Long memberId) {
        validateService.checkAdminMemberIdAndThrow(memberId);
        return memberReviewService.findOneWithReview(memberId);
    }
}
