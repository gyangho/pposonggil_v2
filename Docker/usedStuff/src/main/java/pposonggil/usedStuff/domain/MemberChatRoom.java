package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class MemberChatRoom {
    @Id
    @GeneratedValue
    @Column(name = "member_chat_room_id")
    private Long id;

    @OneToOne(mappedBy = "distanceMemberChatRoom")
    private Distance distance;

    @OneToMany(mappedBy = "chatMessageMemberChatRoom")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
}
