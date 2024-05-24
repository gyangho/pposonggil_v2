package pposonggil.usedStuff.api.ChatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomMessagesDto;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomMessagesService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomMessagesApiController {
    public final ChatRoomMessagesService chatRoomMessagesService;

    /**
     * 채팅을 포함한 전체 채팅방 조회
     * @return 채팅 채팅방 Dto 리스트
     */
    @GetMapping("/api/chatrooms/with-messages")
    public List<ChatRoomMessagesDto> chatRoomsWithMessages() {
        return chatRoomMessagesService.findChatRoomsWithMessages();
    }

    /**
     * 채팅을 포함한 채팅방 상세 조회
     * @param chatRoomId : 조회하려는 채팅방 아이디
     * @return 거래를 포함한 채팅 채팅방 Dto
     */
    @GetMapping("/api/chatroom/with-messages/by-chatroom/{chatRoomId}")
    public ChatRoomMessagesDto getChatRoomWithMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        return chatRoomMessagesService.findOneWithMessages(chatRoomId);
    }

    /**
     * 거래 아이디로 채팅 채팅방 조회
     * @param tradeId : 회원 아이디
     * @return 거래 아이디가 일치하는 채팅을 포함한 메시지채팅방 Dto
     */
    @GetMapping("/api/chatroom/with-messages/by-trade/{tradeId}")
    public ChatRoomMessagesDto findChatRoomWithTradeByTradeId(@PathVariable Long tradeId) {
        return chatRoomMessagesService.findChatRoomWithMessagesByTradeId(tradeId);
    }

    /**
     * 거래 & 채팅 & 채팅방 조회
     * @return 거래, 채팅 정보를 포함한 채팅방 Dto 리스트
     */
    @GetMapping("/api/chatrooms/with-trade-messages")
    public List<ChatRoomMessagesDto> findChatRoomsWithTrade() {
        return chatRoomMessagesService.findChatRoomsWithTradeMessages();
    }
}
