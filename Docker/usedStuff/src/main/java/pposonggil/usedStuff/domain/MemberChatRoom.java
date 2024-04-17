package pposonggil.usedStuff.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class MemberChatRoom {
    @EmbeddedId
    private MemberChatRoomId id;

    @MapsId("memberId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("chatRoomId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

}
