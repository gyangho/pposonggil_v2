package pposonggil.usedStuff.repository.report.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.QMember;
import pposonggil.usedStuff.domain.Report;

import java.util.List;

import static pposonggil.usedStuff.domain.QReport.report;

@Repository
public class CustomReportRepositoryImpl implements CustomReportRepository{
    private final JPAQueryFactory query;
    public CustomReportRepositoryImpl(EntityManager em){
        this.query = new JPAQueryFactory(em);
    }
    QMember sMember = new QMember("sMember");
    QMember oMember = new QMember("oMember");
    @Override
    public List<Report> findAllWithMember() {
        return query
                .select(report)
                .from(report)
                .join(report.reportSubject, sMember).fetchJoin()
                .join(report.reportObject, oMember).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Report> findReportsBySubjectId(Long subjectId){
        return query
                .select(report)
                .from(report)
                .join(report.reportSubject, sMember).fetchJoin()
                .join(report.reportObject, oMember).fetchJoin()
                .where(report.reportSubject.id.eq(subjectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Report> findReportsByObjectId(Long objectId){
        return query
                .select(report)
                .from(report)
                .join(report.reportSubject, sMember).fetchJoin()
                .join(report.reportObject, oMember).fetchJoin()
                .where(report.reportObject.id.eq(objectId))
                .limit(1000)
                .fetch();
    }
}
