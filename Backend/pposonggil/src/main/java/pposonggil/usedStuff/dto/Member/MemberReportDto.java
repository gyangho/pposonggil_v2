package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Report.ReportDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberReportDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private List<ReportDto> reportSubjectDtos;
    private List<ReportDto> reportObjectDtos;

    public static MemberReportDto fromEntity(Member member){
        return MemberReportDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getName())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .reportSubjectDtos(member.getReportSubjects().stream()
                        .map(ReportDto::fromEntity)
                        .collect(Collectors.toList()))
                .reportObjectDtos(member.getReportObjects().stream()
                        .map(ReportDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
