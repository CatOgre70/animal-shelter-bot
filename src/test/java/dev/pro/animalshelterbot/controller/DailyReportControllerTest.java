package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.model.DailyReport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DailyReportControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void addDailyReportTest() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        DailyReport dailyReport = new DailyReport(dateTime, null, 0L, null, null,
                "Жрал тухлую селедку с помойки", "Блевал, но выглядел счастливым",
                "Пока не отучил его жрать то, что находит на помойке");
        URI uri = getURIBuilder().build().toUri();
        ResponseEntity<DailyReport> response = restTemplate.postForEntity(uri, dailyReport, DailyReport.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isGreaterThan(0L);
    }

    @Test
    public void getDailyReportInfoTest() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        DailyReport dailyReport = new DailyReport(dateTime, null, 0L, null, null,
                "Жрал тухлую селедку с помойки", "Блевал, но выглядел счастливым",
                "Пока не отучил его жрать то, что находит на помойке");
        URI uri = getURIBuilder().build().toUri();
        ResponseEntity<DailyReport> response = restTemplate.postForEntity(uri, dailyReport, DailyReport.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isGreaterThan(0L);

        uri = getURIBuilder().path("/{id}").buildAndExpand(response.getBody().getId()).toUri();
        response = restTemplate.getForEntity(uri, DailyReport.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        DailyReport dailyReportActual = response.getBody();
        dailyReport.setId(response.getBody().getId());
        Assertions.assertThat(dailyReportActual).isEqualTo(dailyReport);

    }

    @Test
    public void editDailyReportTest() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        DailyReport dailyReport = new DailyReport(dateTime, null, 0L, null, null,
                "Жрал тухлую селедку с помойки", "Блевал, но выглядел счастливым",
                "Пока не отучил его жрать то, что находит на помойке");
        URI uri = getURIBuilder().build().toUri();
        ResponseEntity<DailyReport> response = restTemplate.postForEntity(uri, dailyReport, DailyReport.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isGreaterThan(0L);

        dailyReport = response.getBody();
        dailyReport.setDiet("Жрал тухлую селедку с помойки, когда сорвался с поводка и убежал");
        dailyReport.setChangeInBehavior("Пока не отучил его жрать то, что находит на помойке, а также убегать от меня");
        restTemplate.put(uri, dailyReport);
        dailyReportHasBeenUpdated(dailyReport, "Жрал тухлую селедку с помойки, когда сорвался с поводка и убежал",
                "Пока не отучил его жрать то, что находит на помойке, а также убегать от меня");
    }

    @Test
    public void deleteDailyReportTest() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        DailyReport dailyReport = new DailyReport(dateTime, null, 0L, null, null,
                "Жрал тухлую селедку с помойки", "Блевал, но выглядел счастливым",
                "Пока не отучил его жрать то, что находит на помойке");
        URI uri = getURIBuilder().build().toUri();
        ResponseEntity<DailyReport> response = restTemplate.postForEntity(uri, dailyReport, DailyReport.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isGreaterThan(0L);

        dailyReport = response.getBody();
        uri = getURIBuilder().path("/{id}").buildAndExpand(dailyReport.getId()).toUri();
        restTemplate.delete(uri);

        response = restTemplate.getForEntity(uri, DailyReport.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    private UriComponentsBuilder getURIBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/dailyreport");
    }

    private void dailyReportHasBeenUpdated(DailyReport dailyReport, String diet, String changeInBehavior) {
        URI uri = getURIBuilder().path("/{id}").buildAndExpand(dailyReport.getId()).toUri();
        ResponseEntity<DailyReport> response = restTemplate.getForEntity(uri, DailyReport.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getDiet()).isEqualTo(diet);
        Assertions.assertThat(response.getBody().getChangeInBehavior()).isEqualTo(changeInBehavior);
    }

}
