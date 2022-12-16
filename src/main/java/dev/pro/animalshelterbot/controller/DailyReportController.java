package dev.pro.animalshelterbot.controller;


import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.DailyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/dailyreport")
public class DailyReportController {

    Logger logger = LoggerFactory.getLogger(DailyReportController.class);
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

    @Operation(
            summary = "add daily report in the database",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "daily report record from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @PostMapping
    public ResponseEntity<DailyReport> addDailyReport(@RequestBody DailyReport dailyReport) {
        DailyReport result = dailyReportService.addDailyReport(dailyReport);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "edit daily report in the database",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "edited daily report record from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @PutMapping
    public ResponseEntity<DailyReport> editDailyReport(@RequestBody DailyReport dailyReport) {
        Optional<DailyReport> result = dailyReportService.editDailyReport(dailyReport);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "delete daily report in the database",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "deleted daily report record from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<DailyReport> deleteDailyReport(@PathVariable Long id) {
        Optional<DailyReport> result = dailyReportService.deleteDailyReport(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation (
            summary = "animal photo uploading",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return String",
                            content = @Content(
                                    mediaType  = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity <String> uploadAvatar(@PathVariable Long id,
                                                @RequestParam MultipartFile avatar) {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        try {
            dailyReportService.uploadPhoto(id, avatar);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.notFound().build();
        }
    }

    @Operation (
            summary = "animal photo downloading",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return photo as byte[]",
                            content = @Content(
                                    mediaType  = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "DailyReport"
    )
    @GetMapping("/{id}/photo/preview")
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable Long id) {
        DailyReport dailyReport = dailyReportService.findDailyReport(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(dailyReport.getMediaType()));
        headers.setContentLength(dailyReport.getSmallPicture().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(dailyReport.getSmallPicture());
    }

}
