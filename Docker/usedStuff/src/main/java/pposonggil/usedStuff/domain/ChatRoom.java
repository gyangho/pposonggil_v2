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
public class ChatRoom extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "chat_board_id")
    private Board chatBoard;

    @Builder.Default
    @OneToMany(mappedBy = "distanceChatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Distance> distances = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "messageChatRoom")
    private List<Message> messages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reviewChatRoom")
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member chatMember;

    private String startTimeString;
    private String endTimeString;

    @Embedded
    private TransactionAddress address;

    public void setChatBoard(Board board) {
        this.chatBoard = board;
    }

    public void setChatMember(Member member) {
        this.chatMember = member;
        member.getChatRooms().add(this);
    }

    public static ChatRoomBuilder builder(Board chatBoard, Member chatMember) {
        if (chatBoard == null || chatMember == null) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }

        return new ChatRoomBuilder()
                .chatBoard(chatBoard)
                .chatMember(chatMember);
    }

    public static ChatRoom buildChatRoom(Board chatBoard,Member chatMember) {
        return ChatRoom.builder(chatBoard, chatMember)
                .chatMember(chatMember)
                .chatBoard(chatBoard)
                .startTimeString(chatBoard.getStartTimeString())
                .endTimeString(chatBoard.getEndTimeString())
                .address(chatBoard.getAddress())
                .build();
    }

}
