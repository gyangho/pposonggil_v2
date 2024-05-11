package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Message;
import pposonggil.usedStuff.dto.MessageDto;
import pposonggil.usedStuff.service.MessageService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MessageApiController {
    private final MessageService messageService;

    /**
     * 메시지 송신
     */
    @PostMapping("/api/message")
    public ResponseEntity<String> createMessage(@RequestBody MessageDto messageDto) {
        Long messageId = messageService.createMessage(messageDto.getSenderId(), messageDto.getMessageChatRoomId(),
                messageDto.getContent());
        return ResponseEntity.ok("Created message with ID: " + messageId);
    }

    /**
     * 특정 메시지 상세 조회
     */
    @GetMapping("/api/messages/{messageId}")
    public MessageDto getMessageByMessageId(@PathVariable Long messageId){
        Message message = messageService.findOne(messageId);
        return MessageDto.fromEntity(message);
    }

    /**
     * 메시지 & 송신자 & 채팅방 조회
     */
    @GetMapping("/api/messages/with-member-chatroom")
    public List<MessageDto> getMessageWithMemberChatRoom() {
        List<Message> messages = messageService.findAllWithMemberChatRoom();
        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

}
