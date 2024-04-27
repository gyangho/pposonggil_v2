package pposonggil.usedStuff.repository.report;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Report;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepository {
    private final EntityManager em;

    public void save(Report report) {
        em.persist(report);
    }

    public Report findOne(Long id) {
        return em.find(Report.class, id);
    }

    public List<Report> findAll() {
        return em.createQuery("select r from Report r", Report.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Report> findAllWithMember() {
        return em.createQuery("select r from Report r " +
                        "join fetch r.reportSubject ms " +
                        "join fetch r.reportObject mo", Report.class)
                .getResultList();
    }
}
