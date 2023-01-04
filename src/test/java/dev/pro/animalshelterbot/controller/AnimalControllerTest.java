package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.constants.AnimalKind;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.service.DailyReportService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimalControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DailyReportService dailyReportService;

    @Test
    public void createAnimalTest() {
        Animal animal = givenAnimalWith("Matroskin", AnimalKind.CAT, "Maine Coon", "Gray");
        ResponseEntity<Animal> response = addAnimalInTheDatabase(getURIBuilder().build().toUri(), animal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    public void getAnimalByIdTest() {
        Animal animal = givenAnimalWith("Matroskin", AnimalKind.CAT, "Maine Coon", "Gray");
        ResponseEntity<Animal> response = addAnimalInTheDatabase(getURIBuilder().build().toUri(), animal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();

        Animal createdAnimal = response.getBody();

        URI uri = getURIBuilder().path("/{id}").buildAndExpand(createdAnimal.getId()).toUri();
        response = restTemplate.getForEntity(uri, Animal.class);
        Assertions.assertThat(response.getBody()).isEqualTo(createdAnimal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void getAnimalTest() {
        Animal animal0 = givenAnimalWith("Matroskin", AnimalKind.CAT, "Maine Coon", "Gray");
        Animal animal1 = givenAnimalWith("Sharik", AnimalKind.DOG, "Cur", "Brown");
        Animal animal2 = givenAnimalWith("Burenka", AnimalKind.COW, "Yaroslavl", "Black and White");
        Animal animal3 = givenAnimalWith("Bobik", AnimalKind.DOG, "Cur", "Black");
        Animal animal4 = givenAnimalWith("Musia", AnimalKind.CAT, "Siberian", "Black with white tie");
        URI uri = getURIBuilder().build().toUri();
        addAnimalInTheDatabase(uri, animal0);
        addAnimalInTheDatabase(uri, animal1);
        addAnimalInTheDatabase(uri, animal2);
        addAnimalInTheDatabase(uri, animal3);
        addAnimalInTheDatabase(uri, animal4);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("name", "a");
        animalsAreFoundByCriteria(queryParams, animal0, animal1, animal2, animal4);

        queryParams.clear();
        queryParams.add("kind", "0");
        animalsAreFoundByCriteria(queryParams, animal0, animal4);

        queryParams.clear();
        queryParams.add("breed", "c");
        animalsAreFoundByCriteria(queryParams, animal0, animal1, animal3);

        queryParams.clear();
        queryParams.add("color", "black");
        animalsAreFoundByCriteria(queryParams, animal2, animal3, animal4);

        queryParams.clear();
        queryParams.add("name", "ik");
        queryParams.add("kind", "1");
        animalsAreFoundByCriteria(queryParams, animal1, animal3);

        queryParams.clear();
        queryParams.add("breed", "c");
        queryParams.add("color", "black");
        animalsAreFoundByCriteria(queryParams, animal3);

        queryParams.clear();
        queryParams.add("breed", "c");
        queryParams.add("color", "b");
        queryParams.add("kind", "1");
        animalsAreFoundByCriteria(queryParams, animal1, animal3);

        queryParams.clear();
        queryParams.add("breed", "c");
        queryParams.add("color", "b");
        queryParams.add("kind", "1");
        queryParams.add("name", "ik");
        animalsAreFoundByCriteria(queryParams, animal1, animal3);

        queryParams.clear();
        uri = getURIBuilder().build().toUri();
        ResponseEntity<Set<Animal>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    public void editAnimalTest() {
        Animal animal = givenAnimalWith("Matroskin", AnimalKind.CAT, "Maine Coon", "Gray");
        ResponseEntity<Animal> response = addAnimalInTheDatabase(getURIBuilder().build().toUri(), animal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();

        animal = response.getBody();

        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        updatingAnimal(animal, "Профессиональный убийца", localDateTime);
        animalHasBeenUpdated(animal, "Профессиональный убийца", localDateTime);

    }

    @Test
    public void deleteAnimalTest() {
        Animal animal = givenAnimalWith("Matroskin", AnimalKind.CAT, "Maine Coon", "Gray");
        ResponseEntity<Animal> response = addAnimalInTheDatabase(getURIBuilder().build().toUri(), animal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();

        animal = response.getBody();

        URI uri = getURIBuilder().cloneBuilder().path("/{id}").buildAndExpand(animal.getId()).toUri();
        restTemplate.delete(uri);
        response = restTemplate.getForEntity(uri, Animal.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

/*
    @Test
    public void uploadAvatarTest() {
        Animal animal = givenAnimalWith("Matroskin", "Cat", "Maine Coon", "Gray");
        ResponseEntity<Animal> response = addAnimalInTheDatabase(getURIBuilder().build().toUri(), animal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();

        animal = response.getBody();

        Path path = Paths.get("D:\\Users\\Vasily\\Downloads\\maine-coon-2.jpg");
        String name = "maine-coon-2.jpg";
        String originalFileName = "maine-coon-2.jpg";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {

        }
        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("avatar", multipartFile.toString());
        URI uri = getURIBuilder().path("/id/avatar").queryParams(queryParams).buildAndExpand(animal.getId()).toUri();
        ResponseEntity<Set<Animal>> response1 = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<Set<Animal>>() {
                });
        Assertions.assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
    }
 */
    @Test
    public void getReportsTest() {
        Animal animal0 = givenAnimalWith("Matroskin", AnimalKind.CAT, "Maine Coon", "Gray");
        Animal animal1 = givenAnimalWith("Sharik", AnimalKind.DOG, "Cur", "Brown");
        Animal animal2 = givenAnimalWith("Burenka", AnimalKind.COW, "Yaroslavl", "Black and White");
        Animal animal3 = givenAnimalWith("Bobik", AnimalKind.DOG, "Cur", "Black");
        Animal animal4 = givenAnimalWith("Musia", AnimalKind.CAT, "Siberian", "Black with white tie");
        URI uri = getURIBuilder().build().toUri();
        addAnimalInTheDatabase(uri, animal0);
        addAnimalInTheDatabase(uri, animal1);
        addAnimalInTheDatabase(uri, animal2);
        addAnimalInTheDatabase(uri, animal3);
        addAnimalInTheDatabase(uri, animal4);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("name", "Sharik");
        uri = getURIBuilder().queryParams(queryParams).build().toUri();
        ResponseEntity<List<Animal>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Animal actualResult = response.getBody().get(0);
        List<DailyReport> expectedResult = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.of(2022, 12, 23, 9, 0, 0).truncatedTo(ChronoUnit.DAYS);
        DailyReport dailyReport1 = new DailyReport(localDateTime, null, 0L, null, null,
                "Оптяь жрал тухлую селедку на помойке", "Потом блевал, но выглядел довольным",
                "Надо приучить его не есть с помойки, а также не убегать от меня во время прогулки");
        dailyReportService.addDailyReport(dailyReport1);
        expectedResult.add(dailyReport1);
        localDateTime = LocalDateTime.of(2022, 12, 22, 9, 0, 0).truncatedTo(ChronoUnit.DAYS);
        DailyReport dailyReport2 = new DailyReport(localDateTime, null, 0L, null, null,
                "Жрал тухлую селедку на помойке", "Потом блевал, но выглядел страшно довольным",
                "Никак не приучу его не есть с помойки, а также не убегать от меня во время прогулки");
        dailyReportService.addDailyReport(dailyReport2);
        expectedResult.add(dailyReport2);
        localDateTime = LocalDateTime.of(2022, 12, 21, 9, 0, 0).truncatedTo(ChronoUnit.DAYS);
        DailyReport dailyReport3 = new DailyReport(localDateTime, null, 0L, null, null,
                "Ел корм, купленный в зоомагазине по рекомендациям лучших собаководов", "Спал, гулял нормально, выглядит хорошо",
                "Попробую отпусть его завтра с поводка");
        dailyReportService.addDailyReport(dailyReport3);
        expectedResult.add(dailyReport3);
        localDateTime = LocalDateTime.of(2022, 12, 20, 9, 0, 0).truncatedTo(ChronoUnit.DAYS);
        DailyReport dailyReport4 = new DailyReport(localDateTime, null, 0L, null, null,
                "Ничего не ел, только пил, по-видимому привыкает к новому месту", "Спал плохо, почти не отходил от моей кровати",
                "Завтра пойдем гулять во двор");
        dailyReportService.addDailyReport(dailyReport4);
        expectedResult.add(dailyReport4);
        uri = getURIBuilder().path("/{id}/reports").buildAndExpand(actualResult.getId()).toUri();
        ResponseEntity<List<DailyReport>> response1 = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertThat(response1.getBody()).isNotNull();
        Assertions.assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<DailyReport> actualResult1 = response1.getBody();
        resetIds(actualResult1);
        Assertions.assertThat(actualResult1).containsExactlyInAnyOrder(dailyReport1, dailyReport2, dailyReport3, dailyReport4);
    }


    private void animalHasBeenUpdated(Animal animal, String features, LocalDateTime localDateTime) {
        URI uri = getURIBuilder().path("/{id}").buildAndExpand(animal.getId()).toUri();
        ResponseEntity<Animal> response = restTemplate.getForEntity(uri, Animal.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getFeatures()).isEqualTo(features);
        Assertions.assertThat(response.getBody().getAdoptionDate()).isEqualTo(localDateTime);
    }

    private void updatingAnimal(Animal animal, String features, LocalDateTime localDateTime) {
        animal.setFeatures(features);
        animal.setAdoptionDate(localDateTime);

        restTemplate.put(getURIBuilder().build().toUri(), animal);
    }

    private void animalsAreFoundByCriteria(MultiValueMap<String, String> queryParams, Animal... animals) {
        URI uri = getURIBuilder().queryParams(queryParams).build().toUri();
        ResponseEntity<Set<Animal>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<Animal>>() {
                });
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Set<Animal> actualResult = response.getBody();
        resetIds(actualResult);
        Assertions.assertThat(actualResult).containsExactlyInAnyOrder(animals);

    }

    private void resetIds(Set<Animal> animals) {
        animals.forEach(it -> it.setId(0L));
    }

    private void resetIds(List<DailyReport> dailyReports) {dailyReports.forEach(dr -> dr.setId(0L));}

    private UriComponentsBuilder getURIBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/animal");
    }

    private Animal givenAnimalWith(String name, AnimalKind kind, String breed, String color) {
        LocalDateTime defaultDateTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        return new Animal(name, kind, breed, color, null, null, 0L, null,
                          defaultDateTime, null);
    }

    private ResponseEntity<Animal> addAnimalInTheDatabase(URI uri, Animal animal) {

        return this.restTemplate.postForEntity(uri, animal, Animal.class);

    }

}
