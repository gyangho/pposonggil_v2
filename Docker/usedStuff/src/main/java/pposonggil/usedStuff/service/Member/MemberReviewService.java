package pposonggil.usedStuff.service.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberReviewDto;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberReviewService {
    private final MemberRepository memberRepository;

    /**
     * 리뷰 정보 포함한 전체 회원 조회
     */
    public List<MemberReviewDto> findMembersWithReviews() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 리뷰 포함해 회원 조회
     */
    public MemberReviewDto findOneWithReview(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);

        return MemberReviewDto.fromEntity(member);
    }
}
