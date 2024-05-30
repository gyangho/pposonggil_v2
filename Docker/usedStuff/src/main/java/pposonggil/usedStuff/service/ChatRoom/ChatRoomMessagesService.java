package pposonggil.usedStuff.service.ChatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomMessagesDto;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomMessagesService {
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅을 포함한 전체 채팅방 조회
     */
    public List<ChatRoomMessagesDto> findChatRoomsWithMessages() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream()
                .map(ChatRoomMessagesDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅을 포함한 채팅방 상세 조회
     */
    public ChatRoomMessagesDto findOneWithMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
        return ChatRoomMessagesDto.fromEntity(chatRoom);
    }

    /**
     * 게시글 아이디로 메시지, 요청자 포함한 채팅방 조회
     */
    public ChatRoomMessagesDto findChatRoomWithBoardRequesterByBoardId(Long boardID) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithBoardRequesterByBoardId(boardID)
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with tradeId: " + boardID));

        return ChatRoomMessagesDto.fromEntity(chatRoom);
    }


    /**
     * 게시글 & 메시지 & 채팅방조회
     */
    public List<ChatRoomMessagesDto> findChatRoomsWithBoardRequester() {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsWithBoardRequester();

        return chatRooms.stream()
                .map(ChatRoomMessagesDto::fromEntity)
                .collect(Collectors.toList());
    }
}
