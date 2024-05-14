package pposonggil.usedStuff.dto;

import lombok.*;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
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
    private String writerNickName;
    private Long chatMemberId;
    private String chatMemberNickName;
    private String startTimeString;
    private String endTimeString;
    private TransactionAddress address;
    private LocalDateTime createdAt;
    public static ChatRoomDto fromEntity(ChatRoom chatRoom){
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getId())
                .boardId(chatRoom.getChatBoard().getId())
                .writerId(chatRoom.getChatBoard().getWriter().getId())
                .writerNickName(chatRoom.getChatBoard().getWriter().getNickName())
                .chatMemberId(chatRoom.getChatMember().getId())
                .chatMemberNickName(chatRoom.getChatMember().getNickName())
                .startTimeString(chatRoom.getStartTimeString())
                .endTimeString(chatRoom.getEndTimeString())
                .address(chatRoom.getAddress())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatRoom toEntity(ChatRoomDto dto, Board chatBoard, Member chatMember) {
        return ChatRoom.builder(chatBoard, chatMember)
                .chatBoard(chatBoard)
                .chatMember(chatMember)
                .createdAt(dto.getCreatedAt())
                .startTimeString(dto.getStartTimeString())
                .endTimeString(dto.getEndTimeString())
                .address(dto.getAddress())
                .build();
    }
}
