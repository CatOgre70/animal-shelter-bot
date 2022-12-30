package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DailyReportRepository is the interface for storing daily client reports (30 days after adoption)
 * Corresponds to the daily_reports table in PostgreSQL. Extends {@link JpaRepository}
 * @see dev.pro.animalshelterbot.model.DailyReport DailyReport
 */
@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> getDailyReportByAnimalId(Long id);

}
