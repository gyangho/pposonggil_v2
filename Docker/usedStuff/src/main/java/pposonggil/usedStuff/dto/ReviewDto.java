package pposonggil.usedStuff.dto;

import lombok.Data;
import pposonggil.usedStuff.domain.Review;

import java.time.LocalDate;

@Data
public class ReviewDto {
    private Long reviewId;
    private Long subjectId;
    private Long objectId;
    private Long boardId;
    private String subjectName;
    private String objectName;
    private Long score;
    private String content;
    private LocalDate createdAt;

    public ReviewDto(Review review) {
        reviewId = review.getId();
        subjectId = review.getReviewSubject().getId();
        objectId = review.getReviewObject().getId();
        boardId = review.getReviewBoard().getId();
        subjectName = review.getReviewSubject().getName();
        objectName = review.getReviewObject().getName();
        score = review.getScore();
        content = review.getContent();
        createdAt = review.getCreatedAt();
    }
}
