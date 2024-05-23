package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.MemberDto;
import pposonggil.usedStuff.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 회원 생성
     * @param memberDto : 회원 Dto
     * @return 성공 --> "Created member with ID : " + memberId
     */
    @PostMapping("/api/member")
    public ResponseEntity<String> createMember(@RequestBody MemberDto memberDto) {
        Long memberId = memberService.createMember(memberDto);
        return ResponseEntity.ok("Created member with ID : " + memberId);
    }

    /**
     * 전체 회원 조회
     * @return 회원 Dto 리스트
     */
    @GetMapping("/api/members")
    public List<MemberDto> members() {
        return memberService.findMembers();
    }

    /**
     * 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 회원 Dto
     */
    @GetMapping("api/member/{memberId}")
    public MemberDto getMember(@PathVariable Long memberId) {
        return memberService.findOne(memberId);
    }

    /**
     * 회원 정보 업데이트
     * @param memberId : 업데이트하려는 회원 아이디
     * @param memberDto : 업데이트하려는 내용이 담긴 회원 Dto
     * @return
     */
    @PutMapping("/api/member/{memberId}")
    public ResponseEntity<String> updateMember(@PathVariable Long memberId, @RequestBody MemberDto memberDto) {
        MemberDto updateMemberDto = memberService.findOne(memberId);
        if (updateMemberDto == null) {
            return ResponseEntity.notFound().build();
        }
        memberService.updateMember(updateMemberDto);

        return ResponseEntity.ok("회원 정보를 업데이트 하였습니다.");
    }

    /**
     * 회원 삭제
     * @param memberId : 삭제하려는 회원 아이디
     * @return 성공 --> "회원을 삭제하였습니다."
     */
    @DeleteMapping("/api/member/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);

        return ResponseEntity.ok("회원을 삭제하였습니다.");
    }
}
