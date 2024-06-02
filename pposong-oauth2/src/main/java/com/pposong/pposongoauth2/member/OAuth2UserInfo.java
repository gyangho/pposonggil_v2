package com.pposong.pposongoauth2.member;

import jakarta.security.auth.message.AuthException;
import lombok.Builder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Collections;
import java.util.Map;

import static com.pposong.pposongoauth2.member.Role.USER;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String provider,
        String profile_image
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new OAuth2AuthenticationException("Oauth_Error");
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile_image((String) attributes.get("picture"))
                .provider("Google")
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile_image((String) profile.get("profile_image_url"))
                .provider("Kakao")
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .profile_image(profile_image)
                .provider(provider)
                .roles(Collections.singleton(USER))
                .build();
    }
}
