package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.dto.Member.MemberDto;

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
        MemberDto memberDto1 = memberService.findOne(memberId1);

        // then
        Optional.of(memberDto1)
                .ifPresent(memberDto -> assertAll("회원 가입 검증",
                        () -> assertEquals("name1", memberDto.getName(), "회원 이름 불일치"),
                        () -> assertEquals("nickName1", memberDto.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals("01011111111", memberDto.getPhone(), "회원 전화번호 불일치"),
                        () -> assertTrue(memberDto.isActivated(), "회원 비활성화")
                ));
    }

    @Test
    public void 회원_탈퇴() throws Exception {

        // when
        memberService.deleteMember(memberId1);

        // then
        List<MemberDto> members = memberService.findMembers();
        assertEquals(1, members.size());
        assertThrows(NoSuchElementException.class, () -> memberService.findOne(memberId1));
    }

    @Test
    public void 중복_닉네임_예외() throws Exception {
        // given
        String nickName = "nickName1";

        // when
        MemberDto newMemberDto = MemberDto.builder()
                .nickName(nickName)
                .build();

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.createMember(newMemberDto);
        });

        MemberDto memberDto1 = memberService.findOne(memberId1);
        Optional.of(memberDto1)
                .ifPresent(memberDto -> assertAll("중복 닉네임 검증",
                        () -> assertEquals("name1", memberDto.getName(), "회원 이름 불일치"),
                        () -> assertEquals("nickName1", memberDto.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals("01011111111", memberDto.getPhone(), "회원 전화번호 불일치"),
                        () -> assertTrue(memberDto.isActivated(), "회원 비활성화")
                ));
    }

    @Test
    public void 중복_전화번호_예외() throws Exception {
        // given
        String nickName3 = "nickName3";
        String phone = "01011111111";

        // when
        MemberDto newMemberDto = MemberDto.builder()
                .nickName(nickName3)
                .phone(phone)
                .build();

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.createMember(newMemberDto);
        });

        MemberDto memberDto1 = memberService.findOne(memberId1);
        Optional.of(memberDto1)
                .ifPresent(memberDto -> assertAll("중복 닉네임 검증",
                        () -> assertEquals("name1", memberDto.getName(), "회원 이름 불일치"),
                        () -> assertEquals("nickName1", memberDto.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals("01011111111", memberDto.getPhone(), "회원 전화번호 불일치"),
                        () -> assertTrue(memberDto.isActivated(), "회원 비활성화")
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

        MemberDto updateMemberDto = MemberDto.builder()
                .memberId(memberId1)
                .name(updateName)
                .nickName(updateNickName)
                .phone(updatePhone)
                .build();

        // when
        memberService.updateMember(updateMemberDto);

        // then
        MemberDto memberDto1= memberService.findOne(memberId1);

        Optional.of(memberDto1)
                .ifPresent(memberDto -> assertAll("회원 가입 검증",
                        () -> assertEquals(updateName, memberDto.getName(), "회원 이름 불일치"),
                        () -> assertEquals(updateNickName, memberDto.getNickName(), "회원 닉네임 불일치"),
                        () -> assertEquals(updatePhone, memberDto.getPhone(), "회원 전화번호 불일치")
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