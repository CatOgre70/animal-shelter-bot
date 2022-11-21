package dev.pro.animalshelterbot.controller;


import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.DailyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailyreport")
public class DailyReportController {
    private final DailyReportService dailyReportService;

    public DailyReportController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }
    @Operation(
            summary = "get information about daily report",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "information about the daily from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @GetMapping("{id}")
    public ResponseEntity<DailyReport> getDailyReportInfo(@PathVariable Long id) {
        DailyReport dailyReport = dailyReportService.findDailyReport(id);
        if (dailyReport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dailyReport);
    }
    @Operation (
            summary = "daily report creation",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return created daily report",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @PostMapping
    public DailyReport createDailyReport(@RequestBody DailyReport dailyReport) {
        return dailyReportService.addDailyReport(dailyReport);
    }
    @Operation (
            summary = "editing daily report parameters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "returns an daily report with modified parameters",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @PutMapping
    public ResponseEntity<DailyReport> editUser(@RequestBody DailyReport dailyReport) {
        DailyReport dailyReport1 = dailyReportService.editDailyReport(dailyReport);
        if (dailyReport1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(dailyReport);
    }
    @Operation(
            summary = "deleting an daily report from the db",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @DeleteMapping("{id}")
    public ResponseEntity deleteDailyReport(@PathVariable Long id) {
        dailyReportService.deleteDailyReport(id);
        return ResponseEntity.ok().build();
    }
}
