package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.model.Animal;
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
import java.util.Set;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimalControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createAnimalTest() {
        Animal animal = givenAnimalWith("Matroskin", "Cat", "Maine Coon", "Gray");
        ResponseEntity<Animal> response = addAnimalInTheDatabase(getURIBuilder().build().toUri(), animal);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    public void getAnimalByIdTest() {
        Animal animal = givenAnimalWith("Matroskin", "Cat", "Maine Coon", "Gray");
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
        Animal animal0 = givenAnimalWith("Matroskin", "Cat", "Maine Coon", "Gray");
        Animal animal1 = givenAnimalWith("Sharik", "Dog", "Cur", "Brown");
        Animal animal2 = givenAnimalWith("Burenka", "Cow", "Yaroslavl", "Black and White");
        Animal animal3 = givenAnimalWith("Bobik", "Dog", "Cur", "Black");
        Animal animal4 = givenAnimalWith("Musia", "Cat", "Siberian", "Black with white tie");
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
        queryParams.add("kind", "cat");
        animalsAreFoundByCriteria(queryParams, animal0, animal4);

        queryParams.clear();
        queryParams.add("breed", "c");
        animalsAreFoundByCriteria(queryParams, animal0, animal1, animal3);

        queryParams.clear();
        queryParams.add("color", "black");
        animalsAreFoundByCriteria(queryParams, animal2, animal3, animal4);

        queryParams.clear();
        queryParams.add("name", "ik");
        queryParams.add("kind", "og");
        animalsAreFoundByCriteria(queryParams, animal1, animal3);

        queryParams.clear();
        queryParams.add("breed", "c");
        queryParams.add("color", "black");
        animalsAreFoundByCriteria(queryParams, animal3);

        queryParams.clear();
        queryParams.add("breed", "c");
        queryParams.add("color", "b");
        queryParams.add("kind", "dog");
        animalsAreFoundByCriteria(queryParams, animal1, animal3);

        queryParams.clear();
        queryParams.add("breed", "c");
        queryParams.add("color", "b");
        queryParams.add("kind", "dog");
        queryParams.add("name", "ik");
        animalsAreFoundByCriteria(queryParams, animal1, animal3);

        queryParams.clear();
        uri = getURIBuilder().build().toUri();
        ResponseEntity<Set<Animal>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<Animal>>() {
                });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    public void editAnimalTest() {
        Animal animal = givenAnimalWith("Matroskin", "Cat", "Maine Coon", "Gray");
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
        Animal animal = givenAnimalWith("Matroskin", "Cat", "Maine Coon", "Gray");
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

    private UriComponentsBuilder getURIBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/animal");
    }

    private Animal givenAnimalWith(String name, String kind, String breed, String color) {
        LocalDateTime defaultDateTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        return new Animal(name, kind, breed, color, null, null, 0L, null,
                          defaultDateTime, null);
    }

    private ResponseEntity<Animal> addAnimalInTheDatabase(URI uri, Animal animal) {

        return this.restTemplate.postForEntity(uri, animal, Animal.class);

    }

}
