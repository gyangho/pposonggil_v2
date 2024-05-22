package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "trade_id")
    private Trade chatTrade;

    @OneToMany(mappedBy = "messageChatRoom")
    private List<Message> messages = new ArrayList<>();

    public void setChatTrade(Trade trade) {
        this.chatTrade = trade;
    }

    public ChatRoom(Trade chatTrade) {
        if (chatTrade == null) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        this.chatTrade = chatTrade;
    }
}