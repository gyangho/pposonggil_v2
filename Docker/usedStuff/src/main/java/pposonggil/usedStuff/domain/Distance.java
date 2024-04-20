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
    @JoinColumn(name = "chat_room_id")
    private ChatRoom distanceChatRoom;

    private Long distance;

    public void setDistanceChatRoom(ChatRoom chatRoom) {
        this.distanceChatRoom = chatRoom;
        chatRoom.getDistances().add(this);
    }
}
