package dev.pro.animalshelterbot.controller;


import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.DailyReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailyreport")
public class DailyReportController {
    private final DailyReportService dailyReportService;

    public DailyReportController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }
    @GetMapping("{id}")
    public ResponseEntity<DailyReport> getDailyReportInfo(@PathVariable Long id) {
        DailyReport dailyReport = dailyReportService.findDailyReport(id);
        if (dailyReport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dailyReport);
    }

    @PostMapping
    public DailyReport createDailyReport(@RequestBody DailyReport dailyReport) {
        return dailyReportService.addDailyReport(dailyReport);
    }

    @PutMapping
    public ResponseEntity<DailyReport> editUser(@RequestBody DailyReport dailyReport) {
        DailyReport dailyReport1 = dailyReportService.editDailyReport(dailyReport);
        if (dailyReport1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(dailyReport);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteDailyReport(@PathVariable Long id) {
        dailyReportService.deleteDailyReport(id);
        return ResponseEntity.ok().build();
    }
}
