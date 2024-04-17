package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Distance {
    @Id
    @GeneratedValue
    @Column(name = "distance_id")
    private Long id;

    @OneToOne()
    @JoinColumn(name = "member_chat_room_id")
    private MemberChatRoom distanceMemberChatRoom;

    private Long distance;
}
