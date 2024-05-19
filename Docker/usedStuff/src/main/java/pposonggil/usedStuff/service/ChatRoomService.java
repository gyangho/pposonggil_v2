package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;

import pposonggil.usedStuff.dto.ChatRoomDto;
import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 전체 채팅방 조회
     */
    public List<ChatRoom> findChatRooms() {
        return chatRoomRepository.findAll();
    }

    /**
     * 채팅방 상세 조회
     */
    public ChatRoom findOne(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 회원 아이디로 채팅방 조회
     */
    public List<ChatRoom> findChatRoomsByMemberId(Long memberId) {
        return chatRoomRepository.findChatRoomsByMemberId(memberId);
    }

    /**
     * 채팅방 & 게시글 & 회원 조회
     */
    public List<ChatRoom> findChatRoomsWithBoardMember() {
        return chatRoomRepository.findChatRoomsWithBoardMember();
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public Long createChatRoom(ChatRoomDto chatRoomDto) {
        Board chatBoard = boardRepository.findById(chatRoomDto.getChatBoardId())
                .orElseThrow(NoSuchElementException::new);
        Member chatMember = memberRepository.findById(chatRoomDto.getChatMemberId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + chatRoomDto.getChatMemberId()));

        ChatRoom chatRoom = ChatRoom.buildChatRoom(chatBoard, chatMember);

        chatRoom.setChatBoard(chatBoard);
        chatRoom.setChatMember(chatMember);

        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }

    /**
     * 채팅방 삭제
     */
    @Transactional
    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
        chatRoomRepository.delete(chatRoom);
    }
}
