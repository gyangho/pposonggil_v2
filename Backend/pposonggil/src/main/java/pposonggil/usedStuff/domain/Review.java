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
public class Review extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_subject_id")
    private Member reviewSubject;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_object_id")
    private Member reviewObject;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trade_id")
    private Trade reviewTrade;

    private Long score;
    private String content;

    public void setReviewSubject(Member member) {
        this.reviewSubject = member;
        member.getReviewSubjects().add(this);
    }

    public void setReviewObject(Member member) {
        this.reviewObject = member;
        member.getReviewObjects().add(this);
    }

    public void setReviewTrade(Trade trade) {
        this.reviewTrade = trade;
        trade.getReviews().add(this);
    }

    public static ReviewBuilder builder(Member reviewSubject, Member reviewObject, Long score) {
        if(reviewSubject == null || reviewObject == null || score == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        if(score < 0 || score > 5 )
            throw new IllegalArgumentException("리뷰 점수 범위(0~5)를 벗어났습니다.");

        return new ReviewBuilder()
                .reviewSubject(reviewSubject)
                .reviewObject(reviewObject)
                .score(score);
    }

    public static Review buildReview(Member reviewSubject, Member reviewObject, Long score, String content) {
        return Review.builder(reviewSubject, reviewObject, score)
                .content(content)
                .build();
    }
}
