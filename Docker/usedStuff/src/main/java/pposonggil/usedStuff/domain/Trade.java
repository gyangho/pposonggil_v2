package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Trade extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "trade_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "chat_board_id")
    private Board tradeBoard;

    @Builder.Default
    @OneToMany(mappedBy = "informationTrade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Information> informations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reviewTrade")
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trade_subject_id")
    private Member tradeSubject;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trade_object_id")
    private Member tradeObject;

    private String startTimeString;
    private String endTimeString;

    @Embedded
    private TransactionAddress address;

    public void setChatBoard(Board board) {
        this.tradeBoard = board;
    }

    public void setTradeSubject(Member member) {
        this.tradeSubject = member;
        member.getTradeSubjects().add(this);
    }

    public void setTradeObject(Member member) {
        this.tradeObject = member;
        member.getTradeObjects().add(this);
    }

    public static TradeBuilder builder(Board tradeBoard, Member tradeSubject, Member tradeObject) {
        if (tradeBoard == null || tradeSubject == null || tradeObject == null) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        return new TradeBuilder()
                .tradeBoard(tradeBoard)
                .tradeSubject(tradeSubject)
                .tradeObject(tradeObject);
    }

    public static Trade buildTrade(Board tradeBoard,Member tradeSubject, Member tradeObject) {
        return Trade.builder(tradeBoard, tradeSubject, tradeObject)
                .tradeBoard(tradeBoard)
                .tradeSubject(tradeSubject)
                .tradeObject(tradeObject)
                .startTimeString(tradeBoard.getStartTimeString())
                .endTimeString(tradeBoard.getEndTimeString())
                .address(tradeBoard.getAddress())
                .build();
    }
}
