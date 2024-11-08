package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.component.TokenProvider;
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
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member.getName(), member.getEmail(), member.getProvider());
    }

    @GetMapping("/find/{name}")
    public Optional<Member> getMember(@PathVariable String name) {
        return memberService.findMemberByName(name);
    }

    @GetMapping("/api/getadmin")
    @ResponseBody
    public String getadmin()
    {
        Long myid = validateService.getMyId();
        if(myid == null)
        {
            throw new AccessDeniedException("401");
        }
        Authentication newAuthentication = validateService.giveAdminAuthentication(myid);
        String token = tokenProvider.generateAccessToken(newAuthentication);

        Optional<Member> existingMember = memberRepository.findById(myid);
        Member memberToUpdate = existingMember.get();
        memberToUpdate.addRole(Role.ADMIN);
        memberRepository.save(memberToUpdate);
        return "<html><body>" +
                "<script> " +
                "localStorage.setItem('token', '" + token + "');" +
                "localStorage.setItem('authority', 'ADMIN');" +
                "alert('어드민 권한을 습득했습니다.');" +
                "window.location.href = '/test';" +  // Optionally redirect to a specific page
                "</script>" +
                "</body></html>";
    }
}
