package pposonggil.usedStuff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ChatRoomMessagesDto {
    private Long chatRoomId;
    private Long chatTradeId;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private List<MessageDto> messages;

    public static ChatRoomMessagesDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomMessagesDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatTradeId(chatRoom.getChatTrade().getId())
                .createdAt(chatRoom.getCreatedAt())
                .updateAt(chatRoom.getUpdateAt())
                .messages(chatRoom.getMessages().stream()
                        .map(MessageDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
