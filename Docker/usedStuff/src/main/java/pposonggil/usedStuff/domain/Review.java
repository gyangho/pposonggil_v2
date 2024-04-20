package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Review {
    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_subject_id")
    private Member reviewSubject;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_object_id")
    private Member reviewObject;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_board_id")
    private Board reviewBoard;

    private Long score;
    private String content;
    private LocalDate createdAt;


    public void setReviewSubject(Member member) {
        this.reviewSubject = member;
        member.getReviewSubjects().add(this);
    }

    public void setReviewObject(Member member) {
        this.reviewObject = member;
        member.getReviewObjects().add(this);
    }

    public void setReviewBoard(Board board) {
        this.reviewBoard = board;
        board.getReviews().add(this);
    }
}
