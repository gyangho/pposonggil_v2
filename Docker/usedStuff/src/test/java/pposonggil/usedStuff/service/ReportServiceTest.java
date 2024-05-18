package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Report;
import pposonggil.usedStuff.domain.ReportType;
import pposonggil.usedStuff.dto.MemberDto;
import pposonggil.usedStuff.dto.ReportDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ReportServiceTest {
    @Autowired
    ReportService reportService;

    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2, memberId3;
    private Long reportId1, reportId2, reportId3;

    @BeforeEach
    void setUp() {
        // 회원 1, 2, 3 생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 신고 1, 2, 3 생성
        // 신고 1 : 회원1 --> 회원 3
        // 신고 2 : 회원2 --> 회원 3
        // 신고 3 : 회원3 --> 회원 1
        reportId1 = createReport(memberId1, memberId3, ReportType.ABUSE.getKrName(), "욕해요");
        reportId2 = createReport(memberId2, memberId3, ReportType.DEFECTIVEUMBRELLA.getKrName(), "불량 우산이에요");
        reportId3 = createReport(memberId3, memberId1, ReportType.ADVERTISEMENT.getKrName(), "광고같아요");
    }

    @Test
    public void 신고_생성() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Report report1 = reportService.findOne(reportId1);

        // then
        Optional.of(report1)
                .ifPresent(report -> assertAll("신고 1 검증",
                        () -> assertEquals(member1, report.getReportSubject(), "신고자 객체 확인"),
                        () -> assertEquals(member1.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals(member1.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                        () -> assertEquals(member3, report.getReportObject(), "피신고자 객체 확인"),
                        () -> assertEquals(member3.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                        () -> assertEquals(ReportType.ABUSE.getKrName(), report.getReportType().getKrName()),
                        () -> assertEquals("욕해요", report.getContent())
                ));
    }

    @Test
    public void 신고자의_아이디로_모든_신고_조회() throws Exception {
        // given
        Long reportId4 = createReport(memberId1, memberId2, ReportType.NOSHOW.getKrName(), "노쇼 했어요");

        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        // then
        List<Report> reports = reportService.findReportsBySubjectId(memberId1);
        assertEquals(2, reports.size());

        // 첫번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member1) && report.getReportObject().equals(member3))
                .findFirst()
                .ifPresent(report -> {
                    assertAll("신고 정보 검증",
                            () -> assertEquals(member1.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                            () -> assertEquals(member1.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals(member1.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                            () -> assertEquals(member3.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                            () -> assertEquals(member3.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(member3.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                            () -> assertEquals(ReportType.ABUSE.getKrName(), report.getReportType().getKrName()),
                            () -> assertEquals("욕해요", report.getContent())
                    );
                });

        // 두번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member1) && report.getReportObject().equals(member2))
                .findFirst()
                .ifPresent(report -> {
                    assertAll("신고 정보 검증",
                            () -> assertEquals(member1.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                            () -> assertEquals(member1.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals(member1.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                            () -> assertEquals(member2.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                            () -> assertEquals(member2.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(member2.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                            () -> assertEquals(ReportType.NOSHOW.getKrName(), report.getReportType().getKrName()),
                            () -> assertEquals("노쇼 했어요", report.getContent())
                    );
                });
    }


    @Test
    public void 피신고자의_아이디로_모든_신고_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        // then
        List<Report> reports = reportService.findReportsByObjectId(memberId3);
        assertEquals(2, reports.size());

        // 첫번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member3) && report.getReportObject().equals(member1))
                .findFirst()
                .ifPresent(report -> {
                    assertAll("신고 정보 검증",
                            () -> assertEquals(member3.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                            () -> assertEquals(member3.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals(member3.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                            () -> assertEquals(member1.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                            () -> assertEquals(member1.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(member1.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                            () -> assertEquals(ReportType.ABUSE.getKrName(), report.getReportType().getKrName()),
                            () -> assertEquals("욕해요", report.getContent())
                    );
                });

        // 두번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member3) && report.getReportObject().equals(member2))
                .findFirst()
                .ifPresent(report -> {
                    assertAll("신고 정보 검증",
                            () -> assertEquals(member3.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                            () -> assertEquals(member3.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals(member3.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                            () -> assertEquals(member2.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                            () -> assertEquals(member2.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(member2.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                            () -> assertEquals(ReportType.DEFECTIVEUMBRELLA.getKrName(), report.getReportType().getKrName()),
                            () -> assertEquals("불량 우산이에요", report.getContent())
                    );
                });
    }

    @Test
    public void 자기_자신을_신고할_수는_없다() throws Exception {
        // when
        assertThrows(IllegalArgumentException.class, () -> {
            createReport(memberId1, memberId1, ReportType.ABUSE.getKrName(), "욕해요");
        });

        // then
        List<Report> reports = reportService.findReports();
        assertEquals(3, reports.size());
    }

    @Test
    public void 신고자_피신고자_정보와_함께_모든신고_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        // then
        List<Report> reports = reportService.findAllWithMember();
        assertEquals(3, reports.size());

        // 첫번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member1) && report.getReportObject().equals(member3))
                .findFirst()
                .ifPresent(report -> assertAll("신고 1 검증",
                        () -> assertEquals(member1, report.getReportSubject(), "신고자 객체 확인"),
                        () -> assertEquals(member1.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals(member1.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                        () -> assertEquals(member3, report.getReportObject(), "피신고자 객체 확인"),
                        () -> assertEquals(member3.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                        () -> assertEquals(ReportType.ABUSE.getKrName(), report.getReportType().getKrName()),
                        () -> assertEquals("욕해요", report.getContent())
                ));

        // 두번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member2) && report.getReportObject().equals(member3))
                .findFirst()
                .ifPresent(report -> assertAll("신고 2 검증",
                        () -> assertEquals(member2, report.getReportSubject(), "신고자 객체 확인"),
                        () -> assertEquals(member2.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                        () -> assertEquals(member2.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals(member2.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                        () -> assertEquals(member3, report.getReportObject(), "피신고자 객체 확인"),
                        () -> assertEquals(member3.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                        () -> assertEquals(ReportType.DEFECTIVEUMBRELLA.getKrName(), report.getReportType().getKrName()),
                        () -> assertEquals("불량 우산이에요", report.getContent())
                ));

        // 세번째 신고 검증
        reports.stream()
                .filter(report -> report.getReportSubject().equals(member3) && report.getReportObject().equals(member1))
                .findFirst()
                .ifPresent(report -> assertAll("신고 3 검증",
                        () -> assertEquals(member3, report.getReportSubject(), "신고자 객체 확인"),
                        () -> assertEquals(member3.getName(), report.getReportSubject().getName(), "신고자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), report.getReportSubject().getNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), report.getReportSubject().getPhone(), "신고자 전화번호 불일치"),
                        () -> assertEquals(member1, report.getReportObject(), "피신고자 객체 확인"),
                        () -> assertEquals(member1.getName(), report.getReportObject().getName(), "피신고자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), report.getReportObject().getNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(member1.getPhone(), report.getReportObject().getPhone(), "피신고자 전화번호 불일치"),
                        () -> assertEquals(ReportType.ADVERTISEMENT.getKrName(), report.getReportType().getKrName()),
                        () -> assertEquals("광고같아요", report.getContent())
                ));
    }

    public Long createMember(String name, String nickName, String phone) {
        MemberDto memberDto = MemberDto.builder()
                .name(name)
                .nickName(nickName)
                .phone(phone)
                .build();

        return memberService.createMember(memberDto);
    }

    public Long createReport(Long subjectId, Long objectId, String reportType, String content) {
        ReportDto reportDto = ReportDto.builder()
                .subjectId(subjectId)
                .objectId(objectId)
                .reportType(reportType)
                .content(content)
                .build();
        return reportService.createReport(reportDto);
    }
}