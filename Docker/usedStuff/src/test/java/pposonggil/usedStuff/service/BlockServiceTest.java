package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.BlockDto;
import pposonggil.usedStuff.dto.MemberDto;

import java.util.List;
import java.util.NoSuchElementException;

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
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Block block1 = blockService.findOne(blockId1);

        // then
        assertNotNull(block1);
        assertEquals(member1, block1.getBlockSubject());
        assertEquals(member3, block1.getBlockObject());
    }

    @Test
    public void 자기_자신을_차단할_수는_없다() throws Exception {
        // when
        assertThrows(IllegalArgumentException.class, () ->{
            createBlock(memberId1, memberId1);
        });

        // then
        List<Block> blocks = blockService.findBlocks();
        assertEquals(3, blocks.size());
    }

    @Test
    public void 똑같은_차단을_여러개_생성할_수_없다() throws  Exception {
        // when
        assertThrows(IllegalArgumentException.class, () ->{
            createBlock(memberId1, memberId3);
        });

        // then
        List<Block> blocks = blockService.findBlocks();
        assertEquals(3, blocks.size());
    }

    @Test
    public void 차단자_피차단자_정보와_함께_모든차단_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        // then
        List<Block> blocks = blockService.findALlWithMember();
        assertEquals(3, blocks.size());

        // 첫번째 차단 검증
        Block findMember1 = blockService.findOne(blockId1);
        assertEquals(member1, findMember1.getBlockSubject());
        assertEquals(member3, findMember1.getBlockObject());

        // 두번째 차단 검증
        Block findMember2 = blockService.findOne(blockId2);
        assertEquals(member2, findMember2.getBlockSubject());
        assertEquals(member3, findMember2.getBlockObject());

        // 세번째 차단 검증
        Block findMember3 = blockService.findOne(blockId3);
        assertEquals(member3, findMember3.getBlockSubject());
        assertEquals(member1, findMember3.getBlockObject());
    }

    @Test
    public void 차단_해제() throws Exception {
        // when
        blockService.deleteBlock(blockId1);
        List<Block> blocks = blockService.findBlocks();

        // then
        assertEquals(2, blocks.size());
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