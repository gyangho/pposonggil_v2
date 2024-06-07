package pposonggil.usedStuff.service.ChatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.ChatRoom;

import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomDto;
import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ChatRoomDto> findChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 상세 조회
     */
    public ChatRoomDto findOne(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 게시글 아이디로 메시지 포함한 채팅방 조회
     */
    public ChatRoomDto findChatRoomWithBoardRequesterByBoardId(Long boardId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithBoardRequesterByBoardId(boardId)
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with boardId: " + boardId));

        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 게시글 아이디에 해당하는 채팅방이 있는지 확인
     */
    public boolean findChatRoomWithBoardRequesterByBoardId2(Long boardId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomWithBoardRequesterByBoardId(boardId);
        return optionalChatRoom.isPresent();
    }

    /**
     * 게시글 & 채팅방조회
     */
    public List<ChatRoomDto> findChatRoomsWithTrade() {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsWithBoardRequester();

        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public Long createChatRoom(ChatRoomDto chatRoomDto) {
        Board chatBoard = boardRepository.findById(chatRoomDto.getBoardId())
                .orElseThrow(() -> new NoSuchElementException("Board not found with id: " + chatRoomDto.getBoardId()));
        Member requester = memberRepository.findById(chatRoomDto.getRequesterId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + chatRoomDto.getRequesterId()));

        if(requester.equals(chatBoard.getWriter())) {
            throw new IllegalArgumentException("자기 자신과 채팅방을 만들 수 없습니다.");
        }

        if(findChatRoomWithBoardRequesterByBoardId2(chatBoard.getId())) {
            chatRoomRepository.findChatRoomWithBoardRequesterByBoardId(chatRoomDto.getBoardId())
                    .ifPresent(chatRoom -> {
                        throw new IllegalArgumentException("이미 채팅방이 생성됐습니다.");
                    });
        }

        ChatRoom chatRoom = ChatRoom.buildChatRoom(chatBoard, requester);

        chatRoom.setChatBoard(chatBoard);
        chatRoom.setRequester(requester);

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
