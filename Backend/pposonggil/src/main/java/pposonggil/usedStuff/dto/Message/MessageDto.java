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
    private Long chatRoomId;
    private String senderNickName;
    private String content;
    private LocalDateTime createdAt;

    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .chatRoomId(message.getMessageChatRoom().getId())
                .senderNickName(message.getSender().getName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
