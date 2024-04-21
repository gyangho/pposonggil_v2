package pposonggil.usedStuff.repository.chatroom.simplequery;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ChatRoomSimpleQueryDto {
    private Long chatRoomId;
    private Long memberId;
    private Long boardId;
    private LocalDate createdAt;
}
