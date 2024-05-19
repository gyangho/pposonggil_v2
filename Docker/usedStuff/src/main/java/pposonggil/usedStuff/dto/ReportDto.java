package pposonggil.usedStuff.dto;

import lombok.*;
import pposonggil.usedStuff.domain.Report;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ReportDto {
    private Long reportId;
    private Long subjectId;
    private Long objectId;
    private String subjectName;
    private String objectName;
    private String reportType;
    private String content;
    private LocalDateTime createdAt;

    public static ReportDto fromEntity(Report report) {
        return ReportDto.builder()
                .reportId(report.getId())
                .subjectId(report.getReportSubject().getId())
                .objectId(report.getReportObject().getId())
                .subjectName(report.getReportSubject().getName())
                .objectName(report.getReportObject().getName())
                .reportType(report.getReportType().getKrName())
                .content(report.getContent())
                .createdAt(report.getCreatedAt())
                .build();
    }
}