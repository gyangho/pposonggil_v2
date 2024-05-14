package pposonggil.usedStuff.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Report;
import pposonggil.usedStuff.domain.ReportType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ReportServiceTest {
    @Autowired
    ReportService reportService;

    @Autowired
    MemberService memberService;

    @Test
    public void 신고_생성() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());


        // when
        Member reportSubject = memberService.findOne(savedId1);
        Member reportObject = memberService.findOne(savedId2);

        // 신고 생성
        String content = "욕해요";
        String reportTypeString = ReportType.ABUSE.getKrName();
        Long reportId = reportService.createReport(savedId1, savedId2, reportTypeString, content);
        Report report = reportService.findOne(reportId);

        // then
        assertNotNull(report);
        assertEquals(reportSubject, report.getReportSubject());
        assertEquals(reportObject, report.getReportObject());
        assertEquals(content, report.getContent());
        assertEquals(ReportType.ABUSE, report.getReportType());
    }

    @Test
    public void 자기_자신을_신고할_수는_없다() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());
        // when
        Member reportSubject = memberService.findOne(savedId1);
        // 신고 생성
        String content = "욕해요";
        String reportTypeString = ReportType.ABUSE.getKrName();

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            reportService.createReport(reportSubject.getId(), reportSubject.getId(), reportTypeString, content);
        });

        List<Report> reports = reportService.findReports();
        assertEquals(0, reports.size());
    }

    @Test
    public void 신고자_피신고자_정보와_함께_모든신고_조회() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        // 회원 3 생성
        String name3 = "name3";
        String nickName3 = "nickName3";
        String phone3 = "01033333333";

        Long savedId3 = memberService.join(Member.builder(nickName3)
                .name(name3)
                .phone(phone3)
                .isActivated(true)
                .build());

        Member member1 = memberService.findOne(savedId1);
        Member member2 = memberService.findOne(savedId2);
        Member member3 = memberService.findOne(savedId3);

        // when
        // 신고1 생성
        // 회원1 --> 회원3 신고
        String content1 = "욕해요";
        String reportTypeString1 = ReportType.ABUSE.getKrName();
        Long reportId1 = reportService.createReport(savedId1, savedId3, reportTypeString1, content1);

        // 신고2 생성
        // 회원2 --> 회원3 신고
        String content2 = "망가진 우산이에요";
        String reportTypeString2 = ReportType.DEFECTIVEUMBRELLA.getKrName();
        Long reportId2 = reportService.createReport(savedId2, savedId3, reportTypeString2, content2);

        // 신고3 생성
        // 회원3 --> 회원1 신고
        String content3 = "광고같아요";
        String reportTypeString3 = ReportType.ADVERTISEMENT.getKrName();
        Long reportId3 = reportService.createReport(savedId3, savedId1, reportTypeString3, content3);

        // then
        List<Report> reports = reportService.findAllWithMember();
        assertEquals(3, reports.size());

        // 첫번째 신고 검증
        Report findReport1 = reportService.findOne(reportId1);
        assertEquals(member1, findReport1.getReportSubject());
        assertEquals(member3, findReport1.getReportObject());
        assertEquals(ReportType.ABUSE, findReport1.getReportType());

        // 두번째 신고 검증
        Report findReport2 = reportService.findOne(reportId2);
        assertEquals(member2, findReport2.getReportSubject());
        assertEquals(member3, findReport2.getReportObject());
        assertEquals(ReportType.DEFECTIVEUMBRELLA, findReport2.getReportType());

        // 세번째 신고 검증
        Report findReport3 = reportService.findOne(reportId3);
        assertEquals(member3, findReport3.getReportSubject());
        assertEquals(member1, findReport3.getReportObject());
        assertEquals(ReportType.ADVERTISEMENT, findReport3.getReportType());
    }
}