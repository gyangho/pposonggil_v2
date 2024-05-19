package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Report;
import pposonggil.usedStuff.dto.ReportDto;
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
        return reportRepository.findById(reportId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 신고자 아이디로 신고 조회
     */
    public List<Report> findReportsBySubjectId(Long subjectId){
        return reportRepository.findReportsBySubjectId(subjectId);
    }

    /**
     * 피신고자 아이디로 신고 조회
     */
    public List<Report> findReportsByObjectId(Long objectId){
        return reportRepository.findReportsByObjectId(objectId);
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
    public Long createReport(ReportDto reportDto){
        Member reportSubject = memberRepository.findById(reportDto.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reportDto.getSubjectId()));
        Member reportObject = memberRepository.findById(reportDto.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reportDto.getObjectId()));

        if(reportSubject.equals(reportObject)){
            throw new IllegalArgumentException("자기 자신을 신고할 수는 없습니다.");
        }

        Report report = Report.buildReport(reportSubject, reportObject, reportDto.getReportType(), reportDto.getContent());

        report.setReportSubject(reportSubject);
        report.setReportObject(reportObject);
        reportRepository.save(report);

        return report.getId();
    }
}

