package pposonggil.usedStuff.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.repository.member.custom.CustomMemberRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    List<Member> findByNickName(String nickName);
    List<Member> findByPhone(String phone);

}
