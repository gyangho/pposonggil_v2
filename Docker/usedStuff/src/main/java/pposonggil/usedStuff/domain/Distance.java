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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "distance_subject_id")
    private Member distanceSubject;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private MemberChatRoom memberChatRoom;

    private Long distance;
}
