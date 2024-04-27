package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom distanceChatRoom;

    private Long curDistance;

    public void setDistanceChatRoom(ChatRoom chatRoom) {
        this.distanceChatRoom = chatRoom;
        chatRoom.getDistances().add(this);
    }
}
