package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@DynamicInsert
public class Board {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "umbrella_transaction_id")
    private UmbrellaTransaction umbrellaTransaction;

    @OneToMany(mappedBy = "pictureBoard")
    private List<Picture> pictures = new ArrayList<>();

    @OneToMany(mappedBy = "timeRangeBoard")
    private List<TimeRange> timeRanges = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private LocalDate date;
    private LocalDate startTime;
    private LocalDate endTime;

    @Embedded
    private TransactionAddress address;

    private Long price;
    private boolean isFreebie;
}
