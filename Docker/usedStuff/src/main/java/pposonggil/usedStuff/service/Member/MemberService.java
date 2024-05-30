package pposonggil.usedStuff.service.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long createMember(MemberDto memberDto) {
        validateDuplicateNickName(memberDto.getNickName());
        validateDuplicatePhone(memberDto.getPhone());

        Member member = Member.buildMember(memberDto.getName(), memberDto.getNickName(), memberDto.getPhone());

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateNickName(String nickName) {
        List<Member> findNickNames = memberRepository.findByNickName(nickName);

        if(!findNickNames.isEmpty())
            throw new IllegalStateException("이미 존재하는 닉네임 입니다.");
    }
    private void validateDuplicatePhone(String phone) {
        List<Member> findPhones = memberRepository.findByPhone(phone);

        if(!findPhones.isEmpty())
            throw new IllegalStateException("이미 존재하는 전화번호 입니다.");
    }

    /**
     * 전체 회원 조회
     */
    public List<MemberDto> findMembers() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 조회
     */
    public MemberDto findOne(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);

        return MemberDto.fromEntity(member);
    }

    /**
     * 회원 정보 업데이트
     */
    @Transactional
    public void updateMember(MemberDto memberDto){
        Member member = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(NoSuchElementException::new);

        // 이름 변경 여부 확인
        if(!member.getName().equals(memberDto.getName()))
            member.setName(memberDto.getName());

        // 닉네임 변경 여부 확인 && 닉네임 중복 체크
        if(!member.getNickName().equals(memberDto.getNickName())){
            validateDuplicateNickName(memberDto.getNickName());
            member.setNickName(memberDto.getNickName());
        }

        // 전화번호 변경 여부 확인 && 전화번호 중복 체크
        if(!member.getPhone().equals(memberDto.getPhone())){
            validateDuplicateNickName(memberDto.getPhone());
            member.setPhone(memberDto.getPhone());
        }
        memberRepository.save(member);
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);
        memberRepository.delete(member);
    }
}
