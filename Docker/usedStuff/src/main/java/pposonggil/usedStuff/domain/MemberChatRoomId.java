package pposonggil.usedStuff.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class MemberChatRoomId implements Serializable {
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    public MemberChatRoomId(Long memberId, Long chatRoomId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
    }
}
