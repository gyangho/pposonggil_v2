package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.dto.Block.BlockDto;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.service.Block.BlockService;
import pposonggil.usedStuff.service.Member.MemberService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BlockServiceTest {
    @Autowired
    BlockService blockService;
    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2, memberId3;
    private Long blockId1, blockId2, blockId3;

    @BeforeEach
    void setUp() {
        // 회원 1, 2, 3 생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 차단 1, 2, 3 생성
        // 차단 1 : 회원1 --> 회원 3
        // 차단 2 : 회원2 --> 회원 3
        // 차단 3 : 회원3 --> 회원 1
        blockId1 = createBlock(memberId1, memberId3);
        blockId2 = createBlock(memberId2, memberId3);
        blockId3 = createBlock(memberId3, memberId1);

    }

    @Test
    public void 차단_생성() throws Exception {
        // when
        BlockDto blockDto1 = blockService.findOne(blockId1);

        // then
        Optional.of(blockDto1)
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId1) && blockDto.getObjectId().equals(memberId3))
                .ifPresent(blockDto -> assertAll("차단 정보 검증",
                        () -> assertEquals("nickName1", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                        () -> assertEquals("nickName3", blockDto.getObjectNickName(), "피차단자 닉네임 불일치")
                ));
    }

    @Test
    void 차단자의_아이디로_모든_차단_조회() throws Exception {
        // given
        Long blockId4 = createBlock(memberId1, memberId2);

        // when

        // then
        List<BlockDto> blockDtos = blockService.findBlocksBySubjectId(memberId1);
        assertEquals(2, blockDtos.size());

        // 첫번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId1) && blockDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 1)",
                            () -> assertEquals("nickName1", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName3", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });

        // 두번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId1) && blockDto.getObjectId().equals(memberId2))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 2)",
                            () -> assertEquals("nickName1", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName2", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });
    }

    @Test
    void 피차단자의_아이디로_모든_차단_조회() throws Exception {
        // when
        List<BlockDto> blockDtos = blockService.findBlocksByObjectId(memberId3);

        // then
        assertEquals(2, blockDtos.size());

        // 첫번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId1) && blockDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 1)",
                            () -> assertEquals("nickName1", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName3", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });

        // 두번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId2) && blockDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 2)",
                            () -> assertEquals("nickName2", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName3", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });
    }

    @Test
    public void 자기_자신을_차단할_수는_없다() throws Exception {
        // when
        assertThrows(IllegalArgumentException.class, () -> {
            createBlock(memberId1, memberId1);
        });

        // then
        List<BlockDto> blockDtos = blockService.findBlocks();
        assertEquals(3, blockDtos.size());
    }

    @Test
    public void 똑같은_차단을_여러개_생성할_수_없다() throws Exception {
        // when
        assertThrows(IllegalArgumentException.class, () -> {
            createBlock(memberId1, memberId3);
        });

        // then
        List<BlockDto> blockDtos = blockService.findBlocks();
        assertEquals(3, blockDtos.size());
    }

    @Test
    public void 차단자_피차단자_정보와_함께_모든차단_조회() throws Exception {
        // when

        // then
        List<BlockDto> blockDtos = blockService.findALlWithMember();
        assertEquals(3, blockDtos.size());

        // 첫번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId1) && blockDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 1)",
                            () -> assertEquals("nickName1", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName3", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });

        // 두번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId2) && blockDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 2)",
                            () -> assertEquals("nickName2", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName3", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });


        // 세번째 차단 검증
        blockDtos.stream()
                .filter(blockDto -> blockDto.getSubjectId().equals(memberId3) && blockDto.getObjectId().equals(memberId1))
                .findFirst()
                .ifPresent(blockDto -> {
                    assertAll("차단 정보 검증 (차단 2)",
                            () -> assertEquals("nickName3", blockDto.getSubjectNickName(), "차단자 닉네임 불일치"),
                            () -> assertEquals("nickName1", blockDto.getObjectNickName(), "피차단자 닉네임 불일치"));
                });
    }

    @Test
    public void 차단_해제() throws Exception {
        // when
        blockService.deleteBlock(blockId1);
        List<BlockDto> blockDtos = blockService.findBlocks();

        // then
        assertEquals(2, blockDtos.size());
        assertThrows(NoSuchElementException.class, () -> blockService.findOne(blockId1));
    }

    public Long createMember(String name, String nickName, String phone) {
        MemberDto memberDto = MemberDto.builder()
                .name(name)
                .nickName(nickName)
                .phone(phone)
                .build();

        return memberService.createMember(memberDto);
    }

    public Long createBlock(Long subjectId, Long objectId) {
        BlockDto blockDto = BlockDto.builder()
                .subjectId(subjectId)
                .objectId(objectId)
                .build();

        return blockService.createBlock(blockDto);
    }
}