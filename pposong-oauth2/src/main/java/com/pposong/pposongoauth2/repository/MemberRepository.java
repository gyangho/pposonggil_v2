package com.pposong.pposongoauth2.repository;

import com.pposong.pposongoauth2.member.Member;
import com.pposong.pposongoauth2.member.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);


}
