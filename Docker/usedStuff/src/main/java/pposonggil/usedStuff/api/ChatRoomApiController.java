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
     * 회원 아이디로 채팅방 조회
     * @param memberId : 회원 아이디
     * @return 해당 회원이 참가중인 모든 채팅방 Dto 리스트
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
     * @return 게시글, 회원 정보를 포함한 채팅방 Dto 리스트
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
