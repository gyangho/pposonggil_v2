package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import pposonggil.usedStuff.component.TokenProvider;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Role;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.service.Auth.TokenService;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Member.MemberService;

import java.sql.Time;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class OauthController {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final ValidateService validateService;

    @GetMapping("/")
    public String home(){return "loginform";}
    @GetMapping("/auth/success")
    public String success() {return "welcome";}
    @GetMapping("/test")
    public String test() {return "test";}

    @GetMapping("/blocked")
    public String blocked(){return "blocked";}

    @ResponseBody
    @GetMapping("/api/blockmember/{memberId}")
    public String blockmember(@PathVariable String memberId)
    {
        validateService.checkAdminAndThrow();
        String message;
        Date now = new Date();
        String refreshToken = null;
        String blockedToken = null;
        Authentication authentication;
        try {
            MemberDto target = memberService.findOne(Long.valueOf(memberId));
            Set<Role> roles = new HashSet<>();
            roles.add(Role.BLOCKED);
            target.setRoles(roles);

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(target.getRoles().toString()));

            User principal = new User(memberId, "",  authorities);

            refreshToken = tokenService.findByAccessTokenOrThrow(Long.valueOf(memberId));
            authentication = new UsernamePasswordAuthenticationToken(principal, refreshToken, authorities);
            blockedToken = tokenProvider.generateBlockedToken(authentication, 1000 * 60L);
            Member targetmember = memberService.updateMember(target);
            System.out.println("BLOCKMEMBER_CONTROLLER: " +targetmember.getRoles());
            tokenService.saveOrUpdate(targetmember, blockedToken);

        }
        catch(NoSuchElementException e)
        {
            message = now.toString() + " 존재하지 않는 사용자Id:" + memberId;
            return message;
        }
        message = now.toString() + " 사용자 이용 정지 완료: " + memberId;
        return message;
    }
}
