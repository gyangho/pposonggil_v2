package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.RefreshToken;
import pposonggil.usedStuff.domain.Role;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Member.MemberService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    @Autowired
    private MemberService memberService;
    private final ValidateService validateService;
    private final MemberRepository memberRepository;

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member.getName(), member.getEmail(), member.getProvider());
    }

    @GetMapping("/find/{name}")
    public Optional<Member> getMember(@PathVariable String name) {
        return memberService.findMemberByName(name);
    }

    @GetMapping("/getadmin")
    public String getadmin()
    {
        Long myid = validateService.getMyId();
        if(myid == null)
        {
            throw new AccessDeniedException("로그인해야 관리자 권한을 받을 수 있습니다.");
        }
        Optional<Member> existingMember = memberRepository.findById(myid);
        Member memberToUpdate = existingMember.get();
        memberToUpdate.addRole(Role.ADMIN);
        memberRepository.save(memberToUpdate);
        //memberToUpdate
        return "<script> window.history.back()</script>";
    }
}
