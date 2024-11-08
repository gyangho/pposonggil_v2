package pposonggil.usedStuff.repository.report;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Report;
import pposonggil.usedStuff.repository.report.custom.CustomReportRepository;


public interface ReportRepository extends JpaRepository<Report, Long>, CustomReportRepository {

}
