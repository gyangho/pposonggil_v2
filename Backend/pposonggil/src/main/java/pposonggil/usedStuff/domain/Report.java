package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Arrays;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Report extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "chat_report_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_subject_id")
    private Member reportSubject;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_object_id")
    private Member reportObject;

    @Enumerated(STRING)
    private ReportType reportType;
    private String content;

    public void setReportSubject(Member member) {
        this.reportSubject = member;
        member.getReportSubjects().add(this);
    }

    public void setReportObject(Member member) {
        this.reportObject = member;
        member.getReportObjects().add(this);
    }

    public static ReportBuilder builder(Member reportSubject, Member reportObject, ReportType reportType) {
        if(reportSubject == null || reportObject == null || reportType == null)
            throw new IllegalArgumentException("필수 파라미터 누락");

        return new ReportBuilder()
                .reportSubject(reportSubject)
                .reportObject(reportObject)
                .reportType(reportType);
    }

    public static Report buildReport(Member reportSubject, Member reportObject, String reportType, String content){
        ReportType enumReportType = Arrays.stream(ReportType.values())
                .filter(e -> e.getKrName().equalsIgnoreCase(reportType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 reportType 값입니다: " + reportType));

        return Report.builder(reportSubject, reportObject, enumReportType)
                .content(content)
                .build();
    }
}
