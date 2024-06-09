package pposonggil.usedStuff.service.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.RefreshToken;
import pposonggil.usedStuff.repository.member.TokenRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveOrUpdate(Member member, String refreshToken) {
        // member ID를 사용하여 기존의 RefreshToken 찾기
        Optional<RefreshToken> existingToken = tokenRepository.findByMember_Id(member.getId());

        if (existingToken.isPresent()) {
            // 기존 토큰이 존재하면, 새로운 refreshToken으로 업데이트
            RefreshToken refreshTokenToUpdate = existingToken.get();
            refreshTokenToUpdate.setRefreshToken(refreshToken);
            tokenRepository.save(refreshTokenToUpdate);
        } else {
            // 존재하지 않는 경우, 새로운 RefreshToken 저장
            RefreshToken refreshTokenToSave = RefreshToken.builder()
                    .member(member)
                    .refreshToken(refreshToken)
                    .build();
            tokenRepository.save(refreshTokenToSave);
        }
    }

    public String findByAccessTokenOrThrow(Long userId) {
        return tokenRepository.findByMember_Id(userId).get().getRefreshToken();
    }
}
