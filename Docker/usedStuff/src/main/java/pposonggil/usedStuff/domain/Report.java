package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Report {
    @Id
    @GeneratedValue
    @Column(name = "chat_report_id")
    private Long id;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_subject_id")
    private Member reportSubject;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_object_id")
    private Member reportObject;

    private String reportType;
    private String content;
    private LocalDate createdAt;

    public void setReportSubject(Member member) {
        this.reportSubject = member;
        member.getReportSubjects().add(this);
    }

    public void setReportObject(Member member) {
        this.reportObject = member;
        member.getReportObjects().add(this);
    }
}
