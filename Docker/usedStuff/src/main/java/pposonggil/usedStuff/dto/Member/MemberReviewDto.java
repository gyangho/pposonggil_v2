package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Review.ReviewDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberReviewDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private List<ReviewDto> reviewSubjectDtos;
    private List<ReviewDto> reviewObjectDtos;

    public static MemberReviewDto fromEntity(Member member){
        return MemberReviewDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getName())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .reviewSubjectDtos(member.getReviewSubjects().stream()
                        .map(ReviewDto::fromEntity)
                        .collect(Collectors.toList()))
                .reviewObjectDtos(member.getReviewObjects().stream()
                        .map(ReviewDto::fromEntity)
                        .collect(Collectors.toList()))

                .build();
    }
}
