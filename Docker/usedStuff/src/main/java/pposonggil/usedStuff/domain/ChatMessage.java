package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class ChatMessage {
    @Id
    @GeneratedValue
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_chat_room_id")
    private MemberChatRoom chatMessageMemberChatRoom;

    @Column(length = 500)
    private String content;

    private boolean isRead;
    private LocalDate createdAt;
}
