package pposonggil.usedStuff.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pposonggil.usedStuff.domain.PrincipalDetails;
import pposonggil.usedStuff.service.Auth.TokenService;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.key}")
    private String key;
    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;
    private static final String KEY_ROLE = "role";
    private final TokenService tokenService;

    @PostConstruct
    private void init() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    private String generateToken(Authentication authentication, long expireTime) {

        Map<String, Object> claims = new HashMap<>();
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(((PrincipalDetails)authentication.getPrincipal()).getName())
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .claim(KEY_ROLE, authorities)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 1. refresh token 발급
    public void generateRefreshToken(Authentication authentication, String accessToken) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveOrUpdate(principalDetails.getName(), refreshToken); // redis에 저장
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        // 2. security의 User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }

    // 3. accessToken 재발급
    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            Long userId = Long.valueOf(parseClaims(accessToken).getSubject());
            String refreshToken  = tokenService.findByAccessTokenOrThrow(userId);

            if (validateToken(refreshToken)) {
                return generateAccessToken(getAuthentication(refreshToken));
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

    }
}

