package pposonggil.usedStuff.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("test1");
        member.setNickName("nickName1");
        member.setPhone("01012345678");

        // when
        Long savedId = memberService.join(member);

        // then
        em.flush();
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 회원탈퇴() throws Exception {
        // given

        // when

        // then
    }

    @Test
    public void 중복_닉네임_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setNickName("nickName1");

        Member member2 = new Member();
        member2.setNickName("nickName1");

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

        Member findMember = memberService.findOne(member1.getId());
        assertEquals(findMember, member1);
    }


    @Test
    public void 중복_전화번호_예외() throws Exception {
        Member member1 = new Member();
        member1.setPhone("01012345678");

        Member member2 = new Member();
        member2.setPhone("01012345678");

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

        Member findMember = memberService.findOne(member1.getId());
        assertEquals(findMember, member1);
    }
}