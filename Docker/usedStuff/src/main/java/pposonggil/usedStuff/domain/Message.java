package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Message {
    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom messageChatRoom;

    @Column(length = 500)
    private String content;

    private boolean isRead;
    private LocalDate createdAt;

    public void setSender(Member member) {
        this.sender = member;
        member.getMessages().add(this);
    }

    public void setMessageChatRoom(ChatRoom chatRoom){
        this.messageChatRoom = chatRoom;
        chatRoom.getMessages().add(this);
    }
}
