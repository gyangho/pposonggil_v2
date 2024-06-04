package pposonggil.usedStuff.dto.ChatRoom;

import lombok.*;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ChatRoomDto {
    private Long chatRoomId;
    private Long boardId;
    private Long writerId;
    private Long requesterId;
    private String writerNickName;
    private String requesterNickName;
    private TransactionAddress address;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getId())
                .boardId(chatRoom.getChatBoard().getId())
                .writerId(chatRoom.getChatBoard().getWriter().getId())
                .requesterId(chatRoom.getRequester().getId())
                .writerNickName(chatRoom.getChatBoard().getWriter().getName())
                .requesterNickName(chatRoom.getRequester().getName())
                .address(chatRoom.getAddress())
                .createdAt(chatRoom.getCreatedAt())
                .updateAt(chatRoom.getUpdateAt())
                .build();
    }
}
