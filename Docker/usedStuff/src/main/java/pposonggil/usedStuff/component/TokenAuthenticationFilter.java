package pposonggil.usedStuff.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String accessToken = resolveToken(request);

        if (tokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        }
        else
        {
            try {
                // 만료되었을 경우 accessToken 재발급
                String reissueAccessToken = tokenProvider.reissueAccessToken(accessToken);

                if (StringUtils.hasText(reissueAccessToken)) {
                    setAuthentication(reissueAccessToken);

                    // 클라이언트에게 JSON 응답으로 새로운 accessToken 전달
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"accessToken\": \"" + reissueAccessToken + "\"}");
                    return;
                }
            }
            catch (NoSuchElementException e)
            {
                System.out.println("CATCH");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (ObjectUtils.isEmpty(token) || !token.startsWith(TOKEN_PREFIX)) {
            System.out.println("NULL======================================++++++");
            return null;
        }
        return token.substring(TOKEN_PREFIX.length());
    }
}
