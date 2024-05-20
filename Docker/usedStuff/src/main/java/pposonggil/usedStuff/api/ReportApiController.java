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
     * @return 신고 Dto 리스트
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
     * @param reportId 조회할 신고 아이디
     * @return 조회한 신고 Dto
     */
    @GetMapping("/api/report/{reportId}")
    public ReportDto getReportByReportId(@PathVariable Long reportId) {
        Report report = reportService.findOne(reportId);
        return ReportDto.fromEntity(report);
    }

    /**
     * 신고자 아이디로 신고 조회
     * @param subjectId 조회할 신고자 아이디
     * @return 신고자 아이디가 일치하는 신고 Dto
     */
    @GetMapping("/api/reports/by-subject/{subjectId}")
    public List<ReportDto> getReportsBySubjectId(@PathVariable Long subjectId) {
        List<Report> reports = reportService.findReportsBySubjectId(subjectId);
        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 피신고자 아이디로 신고 조회
     * @param objectId 조회할 피신고자 아이디
     * @return 피신고자 아이디가 일치하는 신고 Dto
     */
    @GetMapping("/api/reports/by-object/{objectId}")
    public List<ReportDto> getReportsByObjectId(@PathVariable Long objectId) {
        List<Report> reports = reportService.findReportsByObjectId(objectId);
        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 신고 & 신고자 & 피신고자 조회
     * @return 신고자, 피신고자를 포함한 신고 Dto 리스트
     */
    @GetMapping("/api/reports/with-subject-object")
    public List<ReportDto> getReportWithMember() {
        List<Report> reports = reportService.findAllWithMember();
        return reports.stream()
                .map(ReportDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 신고 생성
     * @param reportDto 생성할 reportDto
     * @return 성공 --> "Created report with ID : " + reportId
     */
    @PutMapping("/api/report")
    public ResponseEntity<String> createReport(@RequestBody ReportDto reportDto) {
        Long reportId = reportService.createReport(reportDto);
        return ResponseEntity.ok("Created report with ID : " + reportId);
    }
}
