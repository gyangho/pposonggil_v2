package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@ToString
public class Message extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom messageChatRoom;

    @Column(length = 500)
    private String content;

    private boolean isRead;

    public static MessageBuilder builder(Member sender, ChatRoom messageChatRoom,
                                         String content){
        if(sender == null || messageChatRoom == null || content == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new MessageBuilder()
                .sender(sender)
                .messageChatRoom(messageChatRoom)
                .content(content);
    }

    public void setSender(Member member) {
        this.sender = member;
        member.getMessages().add(this);
    }

    public void setMessageChatRoom(ChatRoom chatRoom){
        this.messageChatRoom = chatRoom;
        chatRoom.getMessages().add(this);
    }

    public static Message buildMessage(Member sender, ChatRoom messageChatRoom,
                                       String content) {
        return Message.builder(sender, messageChatRoom, content)
                .isRead(false)
                .build();
    }

}
