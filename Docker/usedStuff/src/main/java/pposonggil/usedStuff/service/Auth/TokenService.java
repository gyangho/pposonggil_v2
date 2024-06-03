package pposonggil.usedStuff.service.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pposonggil.usedStuff.domain.RefreshToken;
import pposonggil.usedStuff.repository.member.TokenRepository;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveOrUpdate(String name, String refreshToken) {
        RefreshToken refreshToken1 =
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
