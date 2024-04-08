package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@DynamicInsert
public class Review {
    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "umbrella_transaction_id")
    private UmbrellaTransaction reviewUmbrellaTransaction;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reviewed_id")
    private Member reviewed;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reviewer_id")
    private Member reviewer;

    private LocalDate date;
    private Long score;
    private String content;

}
