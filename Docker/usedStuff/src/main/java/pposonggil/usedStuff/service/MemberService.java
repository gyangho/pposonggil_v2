package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateNickName(member.getNickName());
        validateDuplicatePhone(member.getPhone());
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
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 아이디로 조회
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    /**
     * 회원 정보 업데이트
     */
    @Transactional
    public void updateMember(Long memberId, String name, String nickName, String phone){
        Member member = memberRepository.findOne(memberId);

        // 이름 변경 여부 확인
        if(!member.getName().equals(name))
            member.setName(name);

        // 닉네임 변경 여부 확인 && 닉네임 중복 체크
        if(!member.getNickName().equals(nickName)){
            validateDuplicateNickName(nickName);
            member.setNickName(nickName);
        }

        // 전화번호 변경 여부 확인 && 전화번호 중복 체크
        if(!member.getPhone().equals(phone)){
            validateDuplicateNickName(phone);
            member.setPhone(phone);
        }
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        memberRepository.delete(member);
    }
}
