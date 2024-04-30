package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.dto.ChatRoomDto;
import pposonggil.usedStuff.service.ChatRoomService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatRoomApiController {
    public final ChatRoomService chatRoomService;

    /**
     * 전체 채팅방 조회
     */
    @GetMapping("/api/chatrooms")
    public List<ChatRoomDto> chatRooms() {
        List<ChatRoom> chatRooms = chatRoomService.findChatRooms();

        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 상세 조회
     */
    @GetMapping("/api/chatroom/{chatRoomId}")
    public ChatRoomDto getChatRoomByChatRoomId(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.findOne(chatRoomId);
        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 회원 아이디로 채팅방 조회
     */
    @GetMapping("/api/chatrooms/{memberId}")
    public List<ChatRoomDto> getChatRoomsByMemberId(@PathVariable Long memberId) {
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsByMemberId(memberId);
        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 & 게시글 & 회원 조회
     */
    @GetMapping("/api/chatrooms/with-board-member")
    public List<ChatRoomDto> getChatRoomsWithBoardMember() {
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsWithBoardMember();
        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성
     */
    @PostMapping("/api/chatroom")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomDto chatRoomDto){
        Long chatRoomId = chatRoomService.createChatRoom(chatRoomDto.getBoardId(), chatRoomDto.getChatMemberId());
        return ResponseEntity.ok("채팅방을 생성하였습니다. (채팅방 ID : " + chatRoomId + ")");
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/api/chatroom/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId);

        return ResponseEntity.ok("채팅방을 삭제하였습니다.");
    }
}
