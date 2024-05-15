package pposonggil.usedStuff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Report;
import pposonggil.usedStuff.dto.ReportDto;
import pposonggil.usedStuff.service.ReportService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ReportApiController {
    private final ReportService reportService;

    /**
     * 전체 신고 조회
     */
    @GetMapping("/api/reports")
    public List<ReportDto> reports() {
        List<Report> reports = reportService.findReports();

        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 신고 상세 조회
     */
    @GetMapping("/api/report/{reportId}")
    public ReportDto getReportByReportId(@PathVariable Long reportId) {
        Report report = reportService.findOne(reportId);
        return ReportDto.fromEntity(report);
    }

    /**
     * 신고 & 신고자 & 피신고자 조회
     */
    @GetMapping("/api/reports/with-subject-object")
    public List<ReportDto> getReportMember() {
        List<Report> reports = reportService.findAllWithMember();
        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 신고 생성
     */
    @PutMapping("/api/report")
    public ResponseEntity<String> createReport(@RequestBody ReportDto reportDto) {
        Long reportId = reportService.createReport(reportDto);
        return ResponseEntity.ok("Created report with ID : " + reportId);
    }
}
