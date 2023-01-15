package dev.pro.animalshelterbot.controller;

import com.pengrad.telegrambot.TelegramBot;
import dev.pro.animalshelterbot.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.Set;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private TelegramBot telegramBot;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createUserTest() {
        User user = givenUserWith("Vasily", "Demin", "CatOgre",
                                    null, null, 0L);
        ResponseEntity<User> response = addUserInTheDatabase(getURIBuilder().build().toUri(), user);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    public void getUserInfoTest(){

        User user = givenUserWith("Vasily", "Demin", "CatOgre",
                null, null, 0L);
        ResponseEntity<User> response = addUserInTheDatabase(getURIBuilder().build().toUri(), user);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();

        User createdUser = response.getBody();

        URI uri = getURIBuilder().path("/{id}").buildAndExpand(createdUser.getId()).toUri();
        response = restTemplate.getForEntity(uri, User.class);
        Assertions.assertThat(response.getBody()).isEqualTo(createdUser);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void getUserTest(){
        User user0 = givenUserWith("Vasily", "Demin", "CatOgre",
                null, null, 1234567890L);
        User user1 = givenUserWith("Vasily", "Ivanov", "Ivan",
                null, null, 1234567891L);
        User user2 = givenUserWith("Ivan", "Petrov", "Petya",
                null, null, 1234567892L);
        User user3 = givenUserWith("Sergey", "Semenov", "Semen",
                null, null, 1234567893L);
        User user4 = givenUserWith("Olga", "Subbotina", "Deathbed",
                null, null, 1234567894L);
        URI uri = getURIBuilder().build().toUri();
        addUserInTheDatabase(uri, user0);
        addUserInTheDatabase(uri, user1);
        addUserInTheDatabase(uri, user2);
        addUserInTheDatabase(uri, user3);
        addUserInTheDatabase(uri, user4);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("firstName", "va");
        usersAreFoundByCriteria(queryParams, user0, user1, user2);

        queryParams.clear();
        queryParams.add("secondName", "in");
        usersAreFoundByCriteria(queryParams, user0, user4);

        queryParams.clear();
        queryParams.add("nickName", "iv");
        usersAreFoundByCriteria(queryParams, user1);

        queryParams.add("secondName", "iv");
        queryParams.add("firstName", "va");
        usersAreFoundByCriteria(queryParams, user1);

        queryParams.clear();
        queryParams.add("secondName", "bb");
        queryParams.add("firstName", "ol");
        usersAreFoundByCriteria(queryParams, user4);

        queryParams.clear();
        queryParams.add("secondName", "iv");
        queryParams.add("nickName", "iv");
        usersAreFoundByCriteria(queryParams, user1);

        queryParams.clear();
        queryParams.add("firstName", "se");
        queryParams.add("nickName", "se");
        usersAreFoundByCriteria(queryParams, user3);

        queryParams.clear();
        queryParams.add("chatId", "1234567890");
        usersAreFoundByCriteria(queryParams, user0);

        queryParams.add("firstName", "as");
        uri = getURIBuilder().queryParams(queryParams).build().toUri();
        ResponseEntity<Set<User>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<User>>() {
                });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        queryParams.clear();
        uri = getURIBuilder().build().toUri();
        response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<User>>() {
                });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    public void editUserTest() {
        User user = givenUserWith("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        ResponseEntity<User> response = addUserInTheDatabase(getURIBuilder().build().toUri(), user);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
        user = response.getBody();
        updatingUser(user, "1, Red square, Moscow, 100000, Russia", "+79001000000");
        userHasBeenUpdated(user, "1, Red square, Moscow, 100000, Russia", "+79001000000");
    }

    @Test
    public void deleteUserTest() {
        User user = givenUserWith("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        ResponseEntity<User> response = addUserInTheDatabase(getURIBuilder().build().toUri(), user);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
        user = response.getBody();

        URI uri = getURIBuilder().cloneBuilder().path("/{id}").buildAndExpand(user.getId()).toUri();
        restTemplate.delete(uri);
        response = restTemplate.getForEntity(uri, User.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }



    private void userHasBeenUpdated(User user, String newAddress, String newMobilePhone) {
        URI uri = getURIBuilder().path("/{id}").buildAndExpand(user.getId()).toUri();
        ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getAddress()).isEqualTo(newAddress);
        Assertions.assertThat(response.getBody().getMobilePhone()).isEqualTo(newMobilePhone);

    }

    private void updatingUser(User user, String address, String mobilePhone) {
        user.setAddress(address);
        user.setMobilePhone(mobilePhone);

        restTemplate.put(getURIBuilder().build().toUri(), user);
    }

    private ResponseEntity<User> addUserInTheDatabase(URI uri, User user) {

        return this.restTemplate.postForEntity(uri, user, User.class);

    }

    private User givenUserWith(String firstName, String lastName, String nickName,
                               String address, String mobilePhone, Long chatId) {
        return new User(firstName, lastName, nickName, address, mobilePhone, chatId);
    }

    private UriComponentsBuilder getURIBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/user");
    }

    private void usersAreFoundByCriteria(MultiValueMap<String, String> queryParams, User... users) {
        URI uri = getURIBuilder().queryParams(queryParams).build().toUri();
        ResponseEntity<Set<User>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<User>>() {
                });
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Set<User> actualResult = response.getBody();
        resetIds(actualResult);
        Assertions.assertThat(actualResult).containsExactlyInAnyOrder(users);

    }

    private void resetIds(Set<User> users) {
        users.forEach(it -> it.setId(0L));
    }

}
