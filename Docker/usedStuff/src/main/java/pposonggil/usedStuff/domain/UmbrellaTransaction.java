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
public class UmbrellaTransaction {
    @Id
    @GeneratedValue
    @Column(name = "umbrella_transaction_id")
    private Long id;

    @OneToOne(mappedBy = "umbrellaTransaction", fetch = LAZY)
    private Board board;

    @OneToMany(mappedBy = "timeRangeUmbrellaTransaction")
    private List<TimeRange> timeRanges = new ArrayList<>();

    @OneToMany(mappedBy = "reviewUmbrellaTransaction")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "reportUmbrellaTransaction")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "blockUmbrellaTransaction")
    private List<Block> blocks = new ArrayList<>();

    @OneToMany(mappedBy = "distanceUmbrellaTransaction")
    private List<Distance> distances = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id")
    private Member seller;

    private LocalDate date;
    private TransactionAddress address;
    private boolean isSellerCanceled;
    private boolean isBuyerCanceled;
    private boolean isSellerDone;
    private boolean isBuyerDone;


}
