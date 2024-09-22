package pposonggil.usedStuff.service.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberReportDto;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberReportService {
    private final MemberRepository memberRepository;
    
    /**
     * 신고 정보 포함한 전체 회원 조회
     */
    public List<MemberReportDto> findMembersWithReports() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 신고 포함해 회원 조회
     */
    public MemberReportDto findOneWithReport(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);

        return MemberReportDto.fromEntity(member);
    }
}
