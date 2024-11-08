package pposonggil.usedStuff.api.Message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomDto;
import pposonggil.usedStuff.dto.Message.MessageDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomService;
import pposonggil.usedStuff.service.Message.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MessageApiController {
    private final MessageService messageService;
    private final ValidateService validateService;
    private final ChatRoomService chatRoomService;

    /**
     * 송신자
     * 메시지 송신
     *
     * @param messageDto : 메시지 Dto
     * @return 성공 -->
     *          "messageId" : [Id]
     *          "message" : "메시지를 송신하였습니다.."
     */
    @PostMapping("/api/message")
    public ResponseEntity<Object> createMessage(@RequestBody MessageDto messageDto) {
        validateService.validateMemberIdAndThrow(messageDto.getSenderId());
        Long messageId = messageService.createMessage(messageDto);

        Map<String, Object> response = new HashMap<>();
        response.put("messageId", messageId);
        response.put("message", "메시지를 송신하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * admin, 본인
     * 특정 메시지 상세 조회
     *
     * @param messageId : 조회하려는 메시지 아이디
     * @return 조회한 메시지 Dto
     */
    @GetMapping("/api/messages/{messageId}")
    public MessageDto getMessageByMessageId(@PathVariable Long messageId) {
        MessageDto messageDto = messageService.findOne(messageId);
        validateService.validateMemberIdAndThrow(messageDto.getSenderId());
        return messageDto;
    }

    /**
     * 채팅방 참여자
     * 채팅방 아이디로 메시지 조회
     *
     * @param chatRoomId : 조회할 채팅방 아이디
     * @return 채팅방 아이디가 동일한 메시지 Dto 리스트
     */
    @GetMapping("/api/messages/by-chatroom/{chatRoomId}")
    public List<MessageDto> getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        ChatRoomDto chatRoomDto = chatRoomService.findOne(chatRoomId);
        validateService.validateChatMembersAndThrow(chatRoomDto.getRequesterId(), chatRoomDto.getWriterId());

        return messageService.findMessagesByChatRoomId(chatRoomId);
    }

    /**
     * 본인, admin
     * 송신자 아이디로 메시지 조회
     *
     * @param senderId : 조회할 송신자 아이디
     * @return 송신자 아이디가 동일한 메시지 Dto 리스트
     */
    @GetMapping("/api/messages/by-sender/{senderId}")
    public List<MessageDto> getMessagesBySenderId(@PathVariable Long senderId)
    {
        validateService.checkAdminMemberIdAndThrow(senderId);
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
