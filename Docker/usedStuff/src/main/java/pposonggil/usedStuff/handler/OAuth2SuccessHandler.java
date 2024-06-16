package pposonggil.usedStuff.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pposonggil.usedStuff.component.TokenProvider;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.PrincipalDetails;
import pposonggil.usedStuff.domain.RefreshToken;
import pposonggil.usedStuff.domain.Role;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.member.TokenRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

   private final TokenProvider tokenProvider;
   private final TokenRepository tokenRepository;
   private final MemberRepository memberRepository;
    private static final String URI = "https://pposong.ddns.net/auth/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String id = ((PrincipalDetails)authentication.getPrincipal()).getName();
        String nickname =  ((PrincipalDetails) authentication.getPrincipal()).getUsername();
        String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
        Optional<RefreshToken> refreshTokenOptional = tokenRepository.findByMember_Id(Long.valueOf(((PrincipalDetails)authentication.getPrincipal()).getName()));
        String refreshToken = null;
        
        if(refreshTokenOptional.isPresent()) {
            refreshToken = refreshTokenOptional.get().getRefreshToken();
            //유효한 리프레쉬 토큰이 존재한다면(7일 내에 로그인 기록이 있다)
            if (tokenProvider.validateToken(refreshToken)) {
                authentication = tokenProvider.getAuthentication(refreshToken);
                System.out.println("onAuthenticationSuccess: " + authentication);
                Member member = memberRepository.findById(Long.valueOf(id))
                        .orElseThrow();
                if((member.getRoles()).contains(Role.ADMIN))
                {
                    member.deleteRole(Role.ADMIN);
                }
                memberRepository.save(member);
            }
            else
            {
                response.sendRedirect("https://pposong.ddns.net/login");
            }
        }
        else
        {
            //refreshToken 이 만료되었거나 존재하지 않으면 새로운 refreshToken 발행(
            tokenProvider.generateRefreshToken(authentication);
        }
            // accessToken, refreshToken 발급
            String accessToken = tokenProvider.generateAccessToken(authentication);



        // 토큰 전달을 위한 redirect
        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", accessToken)
                .queryParam("id", id)
                .queryParam("nickname", encodedNickname)
                .build().toUriString();

        response.sendRedirect(redirectUrl);

    }
}