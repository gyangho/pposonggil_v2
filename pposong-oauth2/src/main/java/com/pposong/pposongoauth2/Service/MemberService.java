package com.pposong.pposongoauth2.Service;

import com.pposong.pposongoauth2.member.Member;
import com.pposong.pposongoauth2.member.Role;
import com.pposong.pposongoauth2.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class MemberService
{
    @Autowired
    private MemberRepository memberRepository;

    public Member createMember(String name, String email, String provider) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);  // 기본 역할 설정
        Member member = Member.builder()
                .name(name)
                .email(email)
                .provider(provider)
                .roles(roles)
                .build();

        return memberRepository.save(member);
    }
    public Optional<Member> findMemberByName(String name)
    {
        return memberRepository.findByName(name);
    }
}
