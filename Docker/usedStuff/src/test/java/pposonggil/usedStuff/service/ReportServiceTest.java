package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ReportType;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.dto.Report.ReportDto;
import pposonggil.usedStuff.service.Member.MemberService;
import pposonggil.usedStuff.service.Report.ReportService;

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
        ReportDto reportDto1 = reportService.findOne(reportId1);

        // then
        Optional.of(reportDto1)
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId1) && reportDto.getSubjectId().equals(memberId3))
                .ifPresent(reportDto -> assertAll("신고 1 검증",
                        () -> assertEquals("nickName1", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals("nickName3", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(ReportType.ABUSE.getKrName(), reportDto.getReportType()),
                        () -> assertEquals("욕해요", reportDto.getContent())
                ));
    }

    @Test
    public void 신고자의_아이디로_모든_신고_조회() throws Exception {
        // given
        Long reportId4 = createReport(memberId1, memberId2, ReportType.NOSHOW.getKrName(), "노쇼 했어요");

        // when
        List<ReportDto> reportDtos = reportService.findReportsBySubjectId(memberId1);

        // then
        assertEquals(2, reportDtos.size());

        // 첫번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId1) && reportDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(reportDto -> {
                    assertAll("신고 정보 검증 (신고 1)",
                            () -> assertEquals("nickName1", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals("nickName3", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(ReportType.ABUSE.getKrName(), reportDto.getReportType()),
                            () -> assertEquals("욕해요", reportDto.getContent())
                    );
                });

        // 두번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId1) && reportDto.getObjectId().equals(memberId2))
                .findFirst()
                .ifPresent(reportDto -> {
                    assertAll("신고 정보 검증 (신고 2)",
                            () -> assertEquals("nickName1", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals("nickName2", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(ReportType.NOSHOW.getKrName(), reportDto.getReportType()),
                            () -> assertEquals("노쇼 했어요", reportDto.getContent())
                    );
                });
    }

    @Test
    public void 피신고자의_아이디로_모든_신고_조회() throws Exception {
        // when
        List<ReportDto> reportDtos = reportService.findReportsByObjectId(memberId3);

        // then
        assertEquals(2, reportDtos.size());

        // 첫번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId1) && reportDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(reportDto -> {
                    assertAll("신고 정보 검증 (신고 1)",
                            () -> assertEquals("nickName1", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals("nickName3", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(ReportType.ABUSE.getKrName(), reportDto.getReportType()),
                            () -> assertEquals("욕해요", reportDto.getContent())
                    );
                });

        // 두번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId2) && reportDto.getReportId().equals(memberId3))
                .findFirst()
                .ifPresent(reportDto -> {
                    assertAll("신고 정보 검증 (신고 2)",
                            () -> assertEquals("nickName2", reportDto.getSubjectNickName(), "신고자 이름 불일치"),
                            () -> assertEquals("nickName3", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(ReportType.DEFECTIVEUMBRELLA.getKrName(), reportDto.getReportType()),
                            () -> assertEquals("불량 우산이에요", reportDto.getContent())
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
        List<ReportDto> reportDtos = reportService.findReports();
        assertEquals(3, reportDtos.size());
    }

    @Test
    public void 신고자_피신고자_정보와_함께_모든신고_조회() throws Exception {
        // when
        List<ReportDto> reportDtos = reportService.findAllWithMember();

        // then
        assertEquals(3, reportDtos.size());

        // 첫번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId1) && reportDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(reportDto -> {
                    assertAll("신고 정보 검증 (신고 1)",
                            () -> assertEquals("nickName1", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                            () -> assertEquals("nickName3", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                            () -> assertEquals(ReportType.ABUSE.getKrName(), reportDto.getReportType()),
                            () -> assertEquals("욕해요", reportDto.getContent())
                    );
                });

        // 두번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId2) && reportDto.getObjectId().equals(memberId3))
                .findFirst()
                .ifPresent(reportDto -> assertAll("신고 2 검증",
                        () -> assertEquals("nickName2", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals("nickName3", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(ReportType.DEFECTIVEUMBRELLA.getKrName(), reportDto.getReportType()),
                        () -> assertEquals("불량 우산이에요", reportDto.getContent())
                ));

        // 세번째 신고 검증
        reportDtos.stream()
                .filter(reportDto -> reportDto.getSubjectId().equals(memberId3) && reportDto.getObjectId().equals(memberId1))
                .findFirst()
                .ifPresent(reportDto -> assertAll("신고 3 검증",
                        () -> assertEquals("nickName3", reportDto.getSubjectNickName(), "신고자 닉네임 불일치"),
                        () -> assertEquals("nickName1", reportDto.getObjectNickName(), "피신고자 닉네임 불일치"),
                        () -> assertEquals(ReportType.ADVERTISEMENT.getKrName(), reportDto.getReportType()),
                        () -> assertEquals("광고같아요", reportDto.getContent())
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