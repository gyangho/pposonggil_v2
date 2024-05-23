package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.MessageDto;
import pposonggil.usedStuff.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageApiController {
    private final MessageService messageService;

    /**
     * 메시지 송신
     *
     * @param messageDto : 메시지 Dto
     * @return 성공 --> "Created message with ID: " + messageId
     */
    @PostMapping("/api/message")
    public ResponseEntity<String> createMessage(@RequestBody MessageDto messageDto) {
        Long messageId = messageService.createMessage(messageDto);
        return ResponseEntity.ok("Created message with ID: " + messageId);
    }

    /**
     * 특정 메시지 상세 조회
     *
     * @param messageId : 조회하려는 메시지 아이디
     * @return 조회한 메시지 Dto
     */
    @GetMapping("/api/messages/{messageId}")
    public MessageDto getMessageByMessageId(@PathVariable Long messageId) {
        return messageService.findOne(messageId);
    }

    /**
     * 채팅방 아이디로 메시지 조회
     *
     * @param chatRoomId : 조회할 채팅방 아이디
     * @return 채팅방 아이디가 동일한 메시지 Dto 리스트
     */
    @GetMapping("/api/messages/by-chatroom/{chatRoomId}")
    public List<MessageDto> getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        return messageService.findMessagesByChatRoomId(chatRoomId);
    }

    /**
     * 송신자 아이디로 메시지 조회
     *
     * @param senderId : 조회할 송신자 아이디
     * @return 송신자 아이디가 동일한 메시지 Dto 리스트
     */
    @GetMapping("/api/messages/by-sender/{senderId}")
    public List<MessageDto> getMessagesBySenderId(@PathVariable Long senderId) {
        return messageService.findMessagesBySenderId(senderId);
    }

    /**
     * 송신자 & 채팅방 & 메시지 조회
     *
     * @return 송신자, 채팅방 정보를 포함한 메시지 Dto 리스트
     */
    @GetMapping("/api/messages/with-member-chatroom")
    public List<MessageDto> getMessagesWithMemberChatRoom() {
        return messageService.findAllWithMemberChatRoom();
    }
}
