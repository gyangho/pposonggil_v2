package pposonggil.usedStuff.dto.Message;

import lombok.*;
import pposonggil.usedStuff.domain.Message;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MessageDto {
    private Long messageId;
    private Long senderId;
    private Long messageChatRoomId;
    private String senderNickName;
    private String content;
    private LocalDateTime createdAt;

    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .messageChatRoomId(message.getMessageChatRoom().getId())
                .senderNickName(message.getSender().getNickName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
