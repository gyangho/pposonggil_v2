package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Board extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    private String title;
    private String content;

    private String startTimeString;
    private String endTimeString;
    private String imageUrl;

    @Embedded
    private TransactionAddress address;


    private Long price;
    private boolean isFreebie;

    public void setWriter(Member member) {
        this.writer = member;
        member.getBoards().add(this);
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public void changeEndTimeString(String endTimeString) {
        this.endTimeString = endTimeString;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeAddress(TransactionAddress address) {
        this.address = address;
    }

    public void changePrice(Long price) {
        this.price = price;
    }

    public void changeIsFreebie(boolean isFreebie) {
        this.isFreebie = isFreebie;
    }

    public static BoardBuilder builder(Member member, String title, String startTimeString, String endTimeString,
                                       TransactionAddress address, Long price, boolean isFreebie) {
        if (member == null || title == null || startTimeString == null || endTimeString == null
                || address == null || price == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new BoardBuilder()
                .writer(member)
                .title(title)
                .startTimeString(startTimeString)
                .endTimeString(endTimeString)
                .address(address)
                .price(price)
                .isFreebie(isFreebie);
    }
    public static Board buildBoard(Member writer, String title, String content, String startTimeString, String endTimeString, TransactionAddress address, Long price, boolean isFreebie) {
        return Board.builder(writer, title, startTimeString, endTimeString, address, price, isFreebie)
                .content(content)
                .build();
    }
}
