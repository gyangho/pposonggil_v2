package pposonggil.usedStuff.dto.ChatRoom;

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
    private String addressName;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatTradeId(chatRoom.getChatTrade().getId())
                .addressName(chatRoom.getChatTrade().getAddress().getName())
                .createdAt(chatRoom.getCreatedAt())
                .updateAt(chatRoom.getUpdateAt())
                .build();
    }
}
