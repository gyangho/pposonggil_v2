package pposonggil.usedStuff.service.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberTradeDto;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberTradeService {
    private final MemberRepository memberRepository;

    /**
     * 거래 정보 포함한 전체 회원 조회
     */
    public List<MemberTradeDto> findMembersWithTrades() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberTradeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 거래 포함해 회원 조회
     */
    public MemberTradeDto findOneWithTrade(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);

        return MemberTradeDto.fromEntity(member);
    }
}
