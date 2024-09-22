package pposonggil.usedStuff.service.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberBoardDto;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberBoardService {
    private final MemberRepository memberRepository;

    /**
     * 게시글 정보 포함한 전체 회원 조회
     */
    public List<MemberBoardDto> findMembersWithBoards() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberBoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 차단 포함해 회원 조회
     */
    public MemberBoardDto findOneWithBoard(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);

        return MemberBoardDto.fromEntity(member);
    }
}
