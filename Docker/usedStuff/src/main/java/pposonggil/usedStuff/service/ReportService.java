package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Report;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.report.ReportRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    /**
     * 전체 신고 조회
     */
    public List<Report> findReports(){
        return reportRepository.findAll();
    }

    /**
     * 신고 상세 조회
     */
    public Report findOne(Long reportId){
        return reportRepository.findById(reportId).orElseThrow(NoSuchElementException::new);
    }

    /**
     * 신조 & 신고자 & 피신고자 조회
     */
    public List<Report> findAllWithMember() {
        return reportRepository.findAllWithMember();
    }

    /**
     * 신고 생성
     */
    @Transactional
    public Long createReport(Long reportSubjectId, Long reportObjectId, String reportType, String content){
        Member reportSubject = memberRepository.findById(reportSubjectId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reportSubjectId));
        Member reportObject = memberRepository.findById(reportObjectId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reportObjectId));

        if(reportSubject.equals(reportObject)){
            throw new IllegalArgumentException("자기 자신을 신고할 수는 없습니다.");
        }

        Report report = Report.buildReport(reportSubject, reportObject, reportType, content);
        report.setReportSubject(reportSubject);
        report.setReportObject(reportObject);

        reportRepository.save(report);

        return report.getId();
    }
}

