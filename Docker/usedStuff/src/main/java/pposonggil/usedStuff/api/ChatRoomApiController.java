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
     * @return 채팅방 Dto 리스트
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
     * @param chatRoomId : 조회하려는 채팅방 아이디
     * @return 채팅방 Dto
     */
    @GetMapping("/api/chatroom/{chatRoomId}")
    public ChatRoomDto getChatRoomByChatRoomId(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.findOne(chatRoomId);
        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 거래 아이디로 채팅방 조회
     * @param tradeId : 회원 아이디
     * @return 거래 아이디가 일치하는 채팅방 Dto
     */
    @GetMapping("/api/chatroom/{tradeId}")
    public ChatRoomDto getChatRoomsByTradeId(@PathVariable Long tradeId) {
        ChatRoom chatRoom = chatRoomService.findChatRoomByTradeId(tradeId);
        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 거래 & 채팅방 조회
     * @return 거래 정보를 포함한 채팅방 Dto 리스트
     */
    @GetMapping("/api/chatrooms/with-trade")
    public List<ChatRoomDto> getChatRoomsWithTrade() {
            List<ChatRoom> chatRooms = chatRoomService.findChatRoomsWithTrade();
        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성
     * @param chatRoomDto : 채팅방 Dto
     * @return 성공 --> "채팅방을 생성하였습니다. (채팅방 ID : " + chatRoomId + ")"
     */
    @PostMapping("/api/chatroom")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomDto chatRoomDto) {
        Long chatRoomId = chatRoomService.createChatRoom(chatRoomDto);
        return ResponseEntity.ok("채팅방을 생성하였습니다. (채팅방 ID : " + chatRoomId + ")");
    }

    /**
     * 채팅방 삭제
     * @param chatRoomId : 채팅방 아이디
     * @return 성공 --> "채팅방을 삭제하였습니다."
     */
    @DeleteMapping("/api/chatroom/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId);

        return ResponseEntity.ok("채팅방을 삭제하였습니다.");
    }
}
