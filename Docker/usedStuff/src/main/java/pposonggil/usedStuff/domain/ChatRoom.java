package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board chatBoard;

    @Builder.Default
    @OneToMany(mappedBy = "messageChatRoom")
    private List<Message> messages = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "requester_id")
    private Member requester;

    private String startTimeString;
    private String endTimeString;

    @Embedded
    private TransactionAddress address;

    public void setChatBoard(Board board) {
        this.chatBoard = board;
    }
    public void setRequester(Member requester) {
        this.requester = requester;
        requester.getChatRooms().add(this);
    }

    public static ChatRoom.ChatRoomBuilder builder(Board chatBoard, Member requester) {
        if (chatBoard == null || requester == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new ChatRoomBuilder()
                .chatBoard(chatBoard)
                .requester(requester);
    }
    public static ChatRoom buildChatRoom(Board chatBoard, Member requester) {
        return ChatRoom.builder(chatBoard, requester)
                .startTimeString(chatBoard.getStartTimeString())
                .endTimeString(chatBoard.getEndTimeString())
                .address(chatBoard.getAddress())
                .build();
    }
}