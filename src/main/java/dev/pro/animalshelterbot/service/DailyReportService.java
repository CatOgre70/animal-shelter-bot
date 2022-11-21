package dev.pro.animalshelterbot.service;

import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.repository.DailyReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;

    public DailyReportService(DailyReportRepository dailyReportRepository) {
        this.dailyReportRepository = dailyReportRepository;
    }
    /**
     * event recording process
     */
    private final Logger logger = LoggerFactory.getLogger(DailyReportRepository.class);

    /**
     * saving the animal in the database
     * the repository method is used {@link JpaRepository#save(Object)}
     * event recording process
     * @param dailyReport, must not be null
     * @return the user stored in the database
     */
    public DailyReport addDailyReport(DailyReport dailyReport) {
        logger.info("Metod \"UserService.addDailyReport()\" was called");
        return dailyReportRepository.save(dailyReport);
    }

    /**
     * find for an dailyReport by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * @param id user, must not be null
     * @return found user
     */
    public DailyReport findDailyReport(long id) {
        logger.info("Metod \"UserService.findDailyReport()\" was called");
        return dailyReportRepository.findById(id).orElse(null);
    }
    /**
     * edit for an dailyReport by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * fetching data from the database and modifying it
     * @param dailyReport
     * @return making changes to the database
     */
    public DailyReport editDailyReport(DailyReport dailyReport) {
        logger.info("Metod \"DailyReportService.DailyReport()\" was called");
        Optional<DailyReport> optional = dailyReportRepository.findById(dailyReport.getDailyReportId());
        if(!optional.isPresent()) {
            return null;
        }
        else {
            DailyReport fromDb = optional.get();
            fromDb.setDateTime(dailyReport.getDateTime());
            fromDb.setFilePath(dailyReport.getFilePath());
            fromDb.setDiet(dailyReport.getDiet());
            fromDb.setGeneralWellBeing(dailyReport.getGeneralWellBeing());
            fromDb.setChangeInBehavior(dailyReport.getChangeInBehavior());
            return dailyReportRepository.save(fromDb);
        }
    }
    /**
     * delete for an dailyReport by ID in the database
     * the repository method is used {@link JpaRepository#deleteById(Object)}
     * event recording process
     * @param id, must not be null
     */
    public void deleteDailyReport(long id) {
        logger.info("Metod \"UserService.deleteDailyRepor()\" was called");
        dailyReportRepository.deleteById(id);
    }
}
