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
public class Report {
    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "umbrellaTransaction")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "umbrella_transaction_id")
    private UmbrellaTransaction reportUmbrellaTransaction;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reported_id")
    private Member reported;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    private LocalDate date;
    private String type;
    private String content;

}
