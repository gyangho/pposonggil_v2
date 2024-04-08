package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@DynamicInsert
public class TimeRange {
    @Id
    @GeneratedValue
    @Column(name = "time_range_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board timeRangeBoard;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "umbrella_transaction_id")
    private UmbrellaTransaction timeRangeUmbrellaTransaction;

    private LocalDate startTime;
    private LocalDate endTime;
}

