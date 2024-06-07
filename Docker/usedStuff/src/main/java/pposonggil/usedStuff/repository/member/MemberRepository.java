package pposonggil.usedStuff.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.repository.member.custom.CustomMemberRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    Optional<Member> findById(Long aLong);
    Optional<Member> findByName(String name);
    Optional<Member> findByEmail(String email);
}
