package com.pposong.pposongoauth2.Service;

import com.pposong.pposongoauth2.member.RefreshToken;
import com.pposong.pposongoauth2.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveOrUpdate(String name, String refreshToken) {
        RefreshToken refreshToken1=
                RefreshToken.builder()
                .Id(Long.valueOf(name))
                        .refreshToken(refreshToken)
                                .build();
        tokenRepository.save(refreshToken1);
    }

    public String findByAccessTokenOrThrow(Long userId) {
        return tokenRepository.findById(userId).get().getRefreshToken();
    }

}