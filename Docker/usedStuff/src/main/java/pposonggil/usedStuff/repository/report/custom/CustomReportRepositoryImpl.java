package pposonggil.usedStuff.repository.report.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.QMember;
import pposonggil.usedStuff.domain.QReport;
import pposonggil.usedStuff.domain.Report;

import java.util.List;

@Repository
public class CustomReportRepositoryImpl implements CustomReportRepository{
    private final JPAQueryFactory query;
    QReport report = QReport.report;
    QMember subjectMember = new QMember("subjectMember");
    QMember objectMember = new QMember("objectMember");


    public CustomReportRepositoryImpl(EntityManager em){
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Report> findAllWithMember() {
        return query
                .select(report)
                .from(report)
                .join(report.reportSubject, subjectMember).fetchJoin()
                .join(report.reportObject, objectMember).fetchJoin()
                .limit(1000)
                .fetch();
    }
}
