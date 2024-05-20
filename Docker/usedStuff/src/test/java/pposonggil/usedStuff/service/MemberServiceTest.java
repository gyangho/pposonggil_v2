package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.MemberDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2;

    @BeforeEach
    void setUp() {
        // 회원 1, 2 생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
    }

    @Test
    public void 회원_가입() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);

        // then
        Optional.of(member1)
                .ifPresent(member -> assertAll("회원 가입 검증",
                        () -> assertEquals("name1", member.getName(), "회원 이름 불일치"),
                        () -> assertEquals("nickName1", member.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals("01011111111", member.getPhone(), "회원 전화번호 불일치"),
                        () -> assertTrue(member.isActivated(), "회원 비활성화")
                ));
    }

    @Test
    public void 회원_탈퇴() throws Exception {
        // given
        Member member = memberService.findOne(memberId1);

        // when
        memberService.deleteMember(memberId1);

        // then
        List<Member> members = memberService.findMembers();
        assertEquals(1, members.size());
        assertThrows(NoSuchElementException.class, () -> memberService.findOne(memberId1));
    }

    @Test
    public void 중복_닉네임_예외() throws Exception {
        // given
        String nickName = "nickName1";

        // when
        MemberDto memberDto = MemberDto.builder()
                .nickName(nickName)
                .build();

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.createMember(memberDto);
        });

        Member member1 = memberService.findOne(memberId1);
        Optional.of(member1)
                .ifPresent(member -> assertAll("중복 닉네임 검증",
                        () -> assertEquals("name1", member.getName(), "회원 이름 불일치"),
                        () -> assertEquals("nickName1", member.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals("01011111111", member.getPhone(), "회원 전화번호 불일치"),
                        () -> assertTrue(member.isActivated(), "회원 비활성화")
                ));
    }

    @Test
    public void 중복_전화번호_예외() throws Exception {
        // given
        String nickName3 = "nickName3";
        String phone = "01011111111";

        // when
        MemberDto memberDto = MemberDto.builder()
                .nickName(nickName3)
                .phone(phone)
                .build();

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.createMember(memberDto);
        });

        Member member1 = memberService.findOne(memberId1);
        Optional.of(member1)
                .ifPresent(member -> assertAll("중복 닉네임 검증",
                        () -> assertEquals("name1", member.getName(), "회원 이름 불일치"),
                        () -> assertEquals("nickName1", member.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals("01011111111", member.getPhone(), "회원 전화번호 불일치"),
                        () -> assertTrue(member.isActivated(), "회원 비활성화")
                ));    }

    /**
     * 회원 정보 업데이트
     */
    @Test
    public void 회원_정보_업데이트() throws Exception {
        // given
        String updateName = "updateName";
        String updateNickName = "updateNickName";
        String updatePhone = "010122222222";

        MemberDto memberDto = MemberDto.builder()
                .memberId(memberId1)
                .name(updateName)
                .nickName(updateNickName)
                .phone(updatePhone)
                .build();

        // when
        memberService.updateMember(memberDto);

        // then
        Member updateMember = memberService.findOne(memberId1);

        Optional.of(updateMember)
                .ifPresent(member -> assertAll("회원 가입 검증",
                        () -> assertEquals(updateName, member.getName(), "회원 이름 불일치"),
                        () -> assertEquals(updateNickName, member.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals(updatePhone, member.getPhone(), "회원 전화번호 불일치")
                ));
    }

    public Long createMember(String name, String nickName, String phone) {
        MemberDto memberDto = MemberDto.builder()
                .name(name)
                .nickName(nickName)
                .phone(phone)
                .build();

        return memberService.createMember(memberDto);
    }
}