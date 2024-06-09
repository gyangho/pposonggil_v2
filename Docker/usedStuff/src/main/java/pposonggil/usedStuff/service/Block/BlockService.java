package pposonggil.usedStuff.service.Block;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Block.BlockDto;
import pposonggil.usedStuff.repository.block.BlockRepository;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.message.MessageRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public BlockDto findBlocksByBlockID(Long blockId)
    {
        Block block = blockRepository.findById(blockId).orElseThrow(NoSuchElementException::new);
        return BlockDto.fromEntity(block);

    }

    /**
     * 전체 차단 조회
     */
    public List<BlockDto> findBlocks() {
        List<Block> blocks = blockRepository.findAll();
        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 차단 상세 조회
     */
    public BlockDto findOne(Long blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(NoSuchElementException::new);

        return BlockDto.fromEntity(block);
    }

    /**
     * 차단자 아이디로 차단 조회
     */
    public List<BlockDto> findBlocksBySubjectId(Long subjectId) {
        List<Block> blocks = blockRepository.findBlocksBySubjectId(subjectId);

        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 피차단자 아이디로 차단 조회
     */
    public List<BlockDto> findBlocksByObjectId(Long objectId) {
        List<Block> blocks = blockRepository.findBlocksByObjectId(objectId);

        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 차단 & 차단자 & 피차단자 조회
     */
    public List<BlockDto> findALlWithMember() {
        List<Block> blocks = blockRepository.findAllWithMember();
        return blocks.stream()
                .map(BlockDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 차단 생성
     */
    @Transactional
    public Long createBlock(BlockDto blockDto) {
        Member blockSubject = memberRepository.findById(blockDto.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + blockDto.getSubjectId()));
        Member blockObject = memberRepository.findById(blockDto.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + blockDto.getObjectId()));

        if (blockSubject.equals(blockObject)) {
            throw new IllegalArgumentException("자기 자신을 차단할 수는 없습니다.");
        }

        blockRepository.findByBlockSubjectAndBlockObject(blockDto.getSubjectId(), blockDto.getObjectId())
                .ifPresent(block -> {
                    throw new IllegalArgumentException("이미 차단한 사용자 입니다.");
                });

        Block block = new Block(blockSubject, blockObject);

        block.setBlockSubject(blockSubject);
        block.setBlockObject(blockObject);
        blockRepository.save(block);
        ChatRoom chatRoominfo = chatRoomRepository.findChatRoomWithSenderAndReceiver(blockObject.getId(), blockSubject.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채팅방입니다: " + blockObject.getId() + blockSubject.getId()));

        chatRoomRepository.deleteById(chatRoominfo.getId());
        return block.getId();
    }

    /**
     * 차단 해제
     */
    @Transactional
    public void deleteBlock(Long blockObjectId) {
        Block block = blockRepository.findById(blockObjectId)
                .orElseThrow(NoSuchElementException::new);
        blockRepository.delete(block);
    }
}