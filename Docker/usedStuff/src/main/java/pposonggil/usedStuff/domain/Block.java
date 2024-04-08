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
public class Block {
    @Id
    @GeneratedValue
    @Column(name = "block_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "umbrella_transaction_id")
    private UmbrellaTransaction blockUmbrellaTransaction;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blocked_id")
    private Member blocked;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blocker_id")
    private Member blocker;

    private LocalDate date;
}
