package pposonggil.usedStuff.repository.report.simplequery;

import lombok.Data;
import pposonggil.usedStuff.domain.Report;

import java.time.LocalDate;

@Data
public class ReportSimpleQueryDto {
    private Long reportId;
    private Long subjectId;
    private Long objectId;
    private String subjectName;
    private String objectName;
    private String reportType;
    private String content;
    private LocalDate createdAt;

    public ReportSimpleQueryDto(Report report) {
        reportId = report.getId();
        subjectId = report.getReportSubject().getId();
        objectId = report.getReportObject().getId();
        subjectName = report.getReportSubject().getName();
        objectName = report.getReportObject().getName();
        reportType = report.getReportType();
        content = report.getContent();
        createdAt = report.getCreatedAt();
    }
}