package pposonggil.usedStuff.dto;

import lombok.*;
import pposonggil.usedStuff.domain.Review;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;
import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ReviewDto {
    private Long reviewId;
    private Long subjectId;
    private Long objectId;
    private Long tradeId;
    private String subjectNickName;
    private String objectNickName;
    private Long score;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewDto fromEntity(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getId())
                .subjectId(review.getReviewSubject().getId())
                .objectId(review.getReviewObject().getId())
                .tradeId(review.getReviewTrade().getId())
                .subjectNickName(review.getReviewSubject().getNickName())
                .objectNickName(review.getReviewObject().getNickName())
                .score(review.getScore())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
