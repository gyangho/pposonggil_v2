package pposonggil.usedStuff.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Test
    public void 회원_가입() throws Exception {
        // given
        String name = "name";
        String nickName = "nickName";
        String phone = "01011111111";

        // when
        Long savedId = memberService.join(Member.builder(nickName)
                .name(name)
                .phone(phone)
                .isActivated(true)
                .build());

        // then
        Member savedMember = memberService.findOne(savedId);
        assertEquals(name, savedMember.getName());
        assertEquals(nickName, savedMember.getNickName());
        assertEquals(phone, savedMember.getPhone());
        assertTrue(savedMember.isActivated());
    }

    @Test
    public void 회원_탈퇴() throws  Exception{
        // given
        String name = "name";
        String nickName = "nickName";
        String phone = "01011111111";

        Long savedId = memberService.join(Member.builder(nickName)
                .name(name)
                .phone(phone)
                .build());

        // when
        memberService.deleteMember(savedId);

        // then
        assertThrows(NoSuchElementException.class, () -> memberService.findOne(savedId));
    }

    @Test
    public void 중복_닉네임_예외() throws Exception {
        // given
        String nickName = "nickName";

        // when
        Long savedId = memberService.join(Member.builder(nickName).build());

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(Member.builder(nickName).build());
        });

        Member findMember = memberService.findOne(savedId);
        assertNotNull(findMember);
        assertEquals(nickName, findMember.getNickName());
    }

    @Test
    public void 중복_전화번호_예외() throws Exception {
        String nickName1 = "nickName1";
        String nickName2 = "nickName2";
        String phone = "01011111111";

        // when
        Long savedId = memberService.join(Member
                .builder(nickName1)
                .phone(phone)
                .build());

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(Member
                    .builder(nickName2)
                    .phone(phone)
                    .build());
        });

        Member findMember = memberService.findOne(savedId);
        assertNotNull(findMember);
        assertEquals(phone, findMember.getPhone());
    }

    /**
     * 회원 정보 업데이트
     */
    @Test
    public void 회원_정보_업데이트() throws Exception {
        // given
        String name = "name";
        String nickName = "nickName";
        String phone = "01011111111";

        String updateName = "updateName";
        String updateNickName = "updateNickName";
        String updatePhone = "010122222222";

        Member member = Member.builder(nickName)
                .name(name)
                .phone(phone)
                .build();

        Long savedId = memberService.join(member);

        // when
        memberService.updateMember(savedId, updateName, updateNickName, updatePhone);

        // then
        Member updateMember = memberService.findOne(savedId);
        assertEquals(updateName, updateMember.getName());
        assertEquals(updateNickName, updateMember.getNickName());
        assertEquals(updatePhone, updateMember.getPhone());
    }
}