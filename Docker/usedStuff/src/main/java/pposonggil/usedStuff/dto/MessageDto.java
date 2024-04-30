package pposonggil.usedStuff.dto;

import lombok.Data;
import pposonggil.usedStuff.domain.Message;

import java.time.LocalDate;

@Data
public class MessageDto {
    private Long messageId;
    private Long senderId;
    private Long messageChatRoomId;
    private String senderNickName;
    private String content;
    private LocalDate createdAt;

    public MessageDto(Message message) {
        messageId = message.getId();
        senderId = message.getSender().getId();
        messageChatRoomId = message.getMessageChatRoom().getId();
        senderNickName = message.getSender().getNickName();
        content = message.getContent();
        createdAt = message.getCreatedAt();
    }
}
