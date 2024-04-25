package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.MemberDto;
import pposonggil.usedStuff.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 전체 회원 조회
     */
    @GetMapping("/api/members")
    public List<MemberDto> members() {
        List<Member> members = memberService.findMembers();

        return members.stream()
                .map(MemberDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원 상세 정보 조회
     */
    @GetMapping("api/members/{memberId}")
    public ResponseEntity<MemberDto> getMember(@PathVariable Long memberId) {
        Member member = memberService.findOne(memberId);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MemberDto.fromEntity(member));
    }

    /**
     * 회원 정보 업데이트
     */
    @PutMapping("/api/members/{memberId}")
    public ResponseEntity<String> updateMember(@PathVariable Long memberId, @RequestBody MemberDto memberDto) {
        Member member = memberService.findOne(memberId);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        memberService.updateMember(memberId, memberDto.getName(), memberDto.getNickName(), memberDto.getPhone());

        return ResponseEntity.ok("회원 정보를 업데이트 하였습니다.");
    }

    /**
     * 회원 삭제
     */
    @DeleteMapping("/api/members/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);

        return ResponseEntity.ok("회원을 삭제하였습니다.");
    }
}
