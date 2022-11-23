package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.model.User;
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

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createUserTest() {
        User user = givenUserWith("Vasily", "Demin", "CatOgre",
                                    null, null, 0L);
        ResponseEntity<User> response = addUserInTheDatabase(getURIBuilder().build().toUri(), user);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getUserId()).isNotNull();
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



}
