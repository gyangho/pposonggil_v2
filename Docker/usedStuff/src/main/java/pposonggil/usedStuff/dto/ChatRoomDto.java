package pposonggil.usedStuff.dto;

import lombok.*;
import pposonggil.usedStuff.domain.ChatRoom;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ChatRoomDto {
    private Long chatRoomId;
    private Long chatTradeId;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatTradeId(chatRoom.getChatTrade().getId())
                .createdAt(chatRoom.getCreatedAt())
                .updateAt(chatRoom.getUpdateAt())
                .build();
    }
}
