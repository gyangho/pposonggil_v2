package pposonggil.usedStuff.service.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static pposonggil.usedStuff.domain.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class ValidateService {

    public Authentication giveAdminAuthentication(Long myid)
    {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validateMemberIdAndThrow(myid);

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
        System.out.println("GIVEADMIN_UPDATEDAUTHORIES: " + updatedAuthorities);
        updatedAuthorities.add(new SimpleGrantedAuthority(ADMIN.toString()));

        // 기존 인증 정보를 기반으로 새로운 인증 정보 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                updatedAuthorities
        );
        return newAuth;
    }

    public Long getMyId()
    {
        User principal;
        try {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        catch (ClassCastException e)
        {
            throw new AccessDeniedException("비정상적인 접근입니다.");
        }
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // User 객체의 username을 memberId로 사용한다고 가정
        String authenticatedMemberId = principal.getUsername();
        return Long.valueOf(authenticatedMemberId);
    }
    public boolean validateMemberId(Long memberId) {
        // 현재 인증된 사용자 정보 가져오기
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null)
        {
            return false;
        }
        // User 객체의 username을 memberId로 사용한다고 가정
        String authenticatedMemberId = principal.getUsername();
        return authenticatedMemberId.equals(memberId.toString());
    }

    public boolean checkAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");

        return authorities.contains(adminAuthority);
    }

    public void checkAdminAndThrow()
    {
        if (!checkAdmin()) {
            throw new AccessDeniedException("관리자가 아닙니다");
        }
    }

    public void validateMemberIdAndThrow(Long memberId)
    {
        if(!validateMemberId(memberId))
        {
            throw new AccessDeniedException("인증정보가 일치하지 않습니다.");
        }
    }

    public void validateChatMembersAndThrow(Long RequesterId, Long WriterId)
    {
        if(!validateMemberId(RequesterId) && !validateMemberId(WriterId))
        {
            throw new AccessDeniedException("인증정보가 일치하지 않습니다.");
        }
    }


    public void checkAdminMemberIdAndThrow(Long memberId)
    {
        if(!checkAdmin() && !validateMemberId(memberId))
        {
            throw new AccessDeniedException("인증정보가 일치하지 않습니다.");
        }
    }
}
