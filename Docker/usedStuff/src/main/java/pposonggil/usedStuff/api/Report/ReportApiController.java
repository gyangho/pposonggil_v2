package pposonggil.usedStuff.api.Report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Report.ReportDto;
import pposonggil.usedStuff.service.Report.ReportService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportApiController {
    private final ReportService reportService;

    /**
     * 전체 신고 조회
     *
     * @return 신고 Dto 리스트
     */
    @GetMapping("/api/reports")
    public List<ReportDto> reports() {
        return reportService.findReports();
    }

    /**
     * 특정 신고 상세 조회
     *
     * @param reportId 조회할 신고 아이디
     * @return 조회한 신고 Dto
     */
    @GetMapping("/api/report/{reportId}")
    public ReportDto getReportByReportId(@PathVariable Long reportId) {
        return reportService.findOne(reportId);
    }

    /**
     * 신고자 아이디로 신고 조회
     *
     * @param subjectId 조회할 신고자 아이디
     * @return 신고자 아이디가 일치하는 신고 Dto
     */
    @GetMapping("/api/reports/by-subject/{subjectId}")
    public List<ReportDto> getReportsBySubjectId(@PathVariable Long subjectId) {
        return reportService.findReportsBySubjectId(subjectId);
    }

    /**
     * 피신고자 아이디로 신고 조회
     *
     * @param objectId 조회할 피신고자 아이디
     * @return 피신고자 아이디가 일치하는 신고 Dto
     */
    @GetMapping("/api/reports/by-object/{objectId}")
    public List<ReportDto> getReportsByObjectId(@PathVariable Long objectId) {
        return reportService.findReportsByObjectId(objectId);
    }

    /**
     * 신고자 & 피신고자 & 신고 조회
     *
     * @return 신고자, 피신고자를 포함한 신고 Dto 리스트
     */
    @GetMapping("/api/reports/with-member")
    public List<ReportDto> getReportWithMember() {
        return reportService.findAllWithMember();
    }

    /**
     * 신고 생성
     *
     * @param reportDto 생성할 reportDto
     * @return 성공 -->
     *          "reportId" : [Id]
     *          "message" : "신고를 생성하였습니다."
     */
    @PostMapping("/api/report")
    public ResponseEntity<Object> createReport(@RequestBody ReportDto reportDto) {
        Long reportId = reportService.createReport(reportDto);

        Map<String, Object> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("message", "신고를 생성하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
