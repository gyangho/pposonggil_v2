package pposonggil.usedStuff.dto;

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
    private Long chatBoardId;
    private Long writerId;
    private String writerNickName;
    private Long chatMemberId;
    private String chatMemberNickName;
    private String startTimeString;
    private String endTimeString;
    private TransactionAddress address;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatBoardId(chatRoom.getChatBoard().getId())
                .writerId(chatRoom.getChatBoard().getWriter().getId())
                .writerNickName(chatRoom.getChatBoard().getWriter().getNickName())
                .chatMemberId(chatRoom.getChatMember().getId())
                .chatMemberNickName(chatRoom.getChatMember().getNickName())
                .startTimeString(chatRoom.getStartTimeString())
                .endTimeString(chatRoom.getEndTimeString())
                .address(chatRoom.getAddress())
                .createdAt(chatRoom.getCreatedAt())
                .updateAt(chatRoom.getUpdateAt())
                .build();
    }
}
