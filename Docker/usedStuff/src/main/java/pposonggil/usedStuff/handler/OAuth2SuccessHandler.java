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
import pposonggil.usedStuff.domain.PrincipalDetails;
import pposonggil.usedStuff.domain.RefreshToken;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.member.TokenRepository;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

   private final TokenProvider tokenProvider;
   private final TokenRepository tokenRepository;
    private static final String URI = "/auth/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Optional<RefreshToken> refreshTokenOptional = tokenRepository.findByMember_Id(Long.valueOf(((PrincipalDetails)authentication.getPrincipal()).getName()));
        String refreshToken = null;
        if(refreshTokenOptional.isPresent()) {
            refreshToken = refreshTokenOptional.get().getRefreshToken();
            if (tokenProvider.validateToken(refreshToken)) {
                authentication = tokenProvider.getAuthentication(refreshToken);
                System.out.println("onAuthenticationSuccess: " + authentication);
            }
        }
        else
        {
            //refreshToken 이 만료되었거나 존재하지 않으면 새로운 refreshToken 발행
            tokenProvider.generateRefreshToken(authentication);
        }
            // accessToken, refreshToken 발급
            String accessToken = tokenProvider.generateAccessToken(authentication);


        // 토큰 전달을 위한 redirect
        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);

    }
}