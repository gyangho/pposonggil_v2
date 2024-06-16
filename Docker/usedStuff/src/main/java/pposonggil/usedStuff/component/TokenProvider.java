package pposonggil.usedStuff.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pposonggil.usedStuff.domain.PrincipalDetails;
import pposonggil.usedStuff.domain.RefreshToken;
import pposonggil.usedStuff.domain.Role;
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
        String subject =null;
        Map<String, Object> claims = new HashMap<>();
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());
        try{
            subject = ((PrincipalDetails)authentication.getPrincipal()).getName();
        }
        catch (ClassCastException e)
        {
            subject= ((User)authentication.getPrincipal()).getUsername();
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .claim(KEY_ROLE, authorities)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateBlockedToken(Authentication authentication, long expireTime)
    {
        return generateToken(authentication, expireTime);
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 1. refresh token 발급
    public void generateRefreshToken(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);

        tokenService.saveOrUpdate(principalDetails.member(), refreshToken); //DB에 저장
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
                Authentication authentication = getAuthentication(refreshToken);
                return generateAccessToken(authentication);
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        try{
            token.length();
        }
        catch (NullPointerException e)
        {
            System.out.println("NULLPOINTEREXCEPTION");
            return false;
        }
        if (!StringUtils.hasText(token)) {
            System.out.println("VALIDATETOKENNULL");
            return false;
        }
        Claims claims = parseClaims(token);
        System.out.println("ROles: " + claims.get(KEY_ROLE).toString());
        if((claims.get(KEY_ROLE)).toString().contains(Role.BLOCKED.toString()))
        {
            throw new AccessDeniedException("999");
        }
        if(claims.getExpiration().before(new Date()))
        {
            SecurityContextHolder.clearContext();
            tokenService.delete(Long.valueOf(claims.getId()));
            return false; // 토큰이 만료된 경우 false 반환
        }
        return true;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e)
        {
            throw new AccessDeniedException("401");
        }

    }
}

