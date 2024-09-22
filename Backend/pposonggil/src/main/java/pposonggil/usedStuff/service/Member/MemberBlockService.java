package pposonggil.usedStuff.service.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberBlockDto;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberBlockService {
    private final MemberRepository memberRepository;

    /**
     * 차단 정보 포함한 전체 회원 조회
     */
    public List<MemberBlockDto> findMembersWithBlocks() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberBlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 차단 포함해 회원 조회
     */
    public MemberBlockDto findOneWithBlock(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);

        return MemberBlockDto.fromEntity(member);
    }
}
