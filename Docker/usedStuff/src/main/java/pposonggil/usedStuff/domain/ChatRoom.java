package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class ChatRoom {
    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @OneToOne(mappedBy = "chatRoom", fetch = LAZY)
    private Board chatBoard;

    @OneToMany(mappedBy = "distanceChatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Distance> distances = new ArrayList<>();

    @OneToMany(mappedBy = "messageChatRoom")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "transactionChatRoom")
    private List<TransactionInformation> transactionInformations = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member chatMember;

    public void setChatBoard(Board board) {
        this.chatBoard = board;
    }

    public void setChatMember(Member member) {
        this.chatMember = member;
        member.getChatRooms().add(this);
    }

}
