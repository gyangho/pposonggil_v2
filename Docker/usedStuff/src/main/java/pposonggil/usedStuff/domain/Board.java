package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
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
public class Board extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "imageBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reviewBoard")
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    private String title;
    private String content;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String startTimeString;
    private String endTimeString;

    @Embedded
    private TransactionAddress address;

    private Long price;
    private boolean isFreebie;

    public static BoardBuilder builder(Member member, String title, LocalDateTime startTime,
                                       LocalDateTime endTime, TransactionAddress address, Long price) {
        if (member == null || title == null || startTime == null || endTime == null
                || address == null || price == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new BoardBuilder()
                .writer(member)
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .address(address)
                .price(price);
    }

    public void setWriter(Member member) {
        this.writer = member;
        member.getBoards().add(this);
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public void setEndTimeString(String endTimeString) {
        this.endTimeString = endTimeString;
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

    public void changeStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void changeEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    public static Board buildBoard(Member writer, String title, String content, LocalDateTime startTime,
                                   LocalDateTime endTime, TransactionAddress address, Long price, boolean isFreebie) {
        return Board.builder(writer, title, startTime, endTime, address, price)
                .content(content)
                .isFreebie(isFreebie)
                .build();
    }
}
