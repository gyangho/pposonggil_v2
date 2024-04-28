package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;

import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;

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
        return chatRoomRepository.findOne(chatRoomId);
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
        return chatRoomRepository.findWithMemberBoard();
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public Long createChatRoom(Long chatBoardId, Long chatMemberId) {
        Board chatBoard = boardRepository.findOne(chatBoardId);
        Member chatMember = memberRepository.findOne(chatMemberId);

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
        ChatRoom chatRoom = chatRoomRepository.findOne(chatRoomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        chatRoomRepository.delete(chatRoom);
    }
}
