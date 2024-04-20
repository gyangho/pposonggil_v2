package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "transaction_information_id")
    private TransactionInformation transactionInformation;

    @OneToMany(mappedBy = "imageBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "reviewBoard")
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    private String title;
    private LocalDate date;
    private LocalDate startTime;
    private LocalDate endTime;

    private Long price;
    private boolean isFreebie;

    public void setWriter(Member member) {
        this.writer = member;
        member.getBoards().add(this);
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        chatRoom.setChatBoard(this);
    }
}
