package dev.pro.animalshelterbot.service;
import dev.pro.animalshelterbot.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.util.*;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Test
    public void addUserTest() {
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user = userService.addUser(user);

        Assertions.assertNotNull(user);
        Assertions.assertTrue(user.getId() != 0L);
    }

    @Test
    public void findUserTest() {
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user = userService.addUser(user);
        Assertions.assertNotNull(user);
        Assertions.assertTrue(user.getId() != 0L);

        User userFound = userService.findUser(user.getId());
        Assertions.assertEquals(user, userFound);

        userFound = userService.findUser(user.getId()+2_000_000L);
        Assertions.assertNull(userFound);
    }

    @Test
    public void editUserTest() {
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user = userService.addUser(user);
        Assertions.assertNotNull(user);
        Assertions.assertTrue(user.getId() != 0L);
        user.setMobilePhone("+79001234567");
        user.setAddress("1, Red Square, Moscow, 100000, Russia");

        User userEdited = userService.editUser(user);
        Assertions.assertEquals(user, userEdited);
    }

    @Test
    public void deleteUserTest() {
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user = userService.addUser(user);
        Assertions.assertNotNull(user);
        Assertions.assertTrue(user.getId() != 0L);

        userService.deleteUser(user.getId());

        Assertions.assertNull(userService.findUser(user.getId()));
    }

    @Test
    public void getAllUserTest() {
        User user0 = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        User user1 = new User("Alexander", "Petrov", "AP", null,
                null, 1234567891L);
        User user2 = new User("Sergey", "Pushkin", "Pushok", null,
                null, 1234567892L);
        User user3 = new User("Ivan", "Tolstoi", "Tolstyi", null,
                null, 1234567893L);
        User user4 = new User("Petr", "Ivanov", "Ivan", null,
                null, 1234567894L);
        userService.addUser(user0);
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);

        Collection<User> actual = userService.getAllUser();
        usersAreFoundByCriteria(actual, user0, user1, user2, user3, user4);
    }

    @Test
    public void findByThreeSubstrings() {
        User user0 = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        User user1 = new User("Alexander", "Petrov", "AP", null,
                null, 1234567891L);
        User user2 = new User("Sergey", "Pushkin", "Pushok", null,
                null, 1234567892L);
        User user3 = new User("Ivan", "Tolstoi", "Tolstyi", null,
                null, 1234567893L);
        User user4 = new User("Petr", "Ivanov", "Ivan", null,
                null, 1234567894L);
        userService.addUser(user0);
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);

        Collection<User> actual = userService.findByThreeSubstrings("a", null, null);
        usersAreFoundByCriteria(actual, user0, user1, user3);

        actual = userService.findByThreeSubstrings(null, "e", null);
        usersAreFoundByCriteria(actual, user0, user1);

        actual = userService.findByThreeSubstrings(null, null, "i");
        usersAreFoundByCriteria(actual, user3, user4);

        actual = userService.findByThreeSubstrings(null, null, null);
        usersAreFoundByCriteria(actual, user0, user1, user2, user3, user4);

        actual = userService.findByThreeSubstrings("y", "in", null);
        usersAreFoundByCriteria(actual, user0, user2);

        actual = userService.findByThreeSubstrings("v", null, "O");
        usersAreFoundByCriteria(actual, user0, user3);

        actual = userService.findByThreeSubstrings(null, "ov", "a");
        usersAreFoundByCriteria(actual, user1, user4);

        actual = userService.findByThreeSubstrings("A", "ov", "a");
        usersAreFoundByCriteria(actual, user1);
    }

    @Test
    public void findByChatIdTest() {
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user = userService.addUser(user);

        User actual = userService.findByChatId(1234567890L);
        Assertions.assertEquals(user, actual);
    }

    @Test
    public void checkByChatIdTest() {
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        userService.addUser(user);

        Assertions.assertTrue(userService.checkByChatId(1234567890L));
        Assertions.assertFalse(userService.checkByChatId(12345L));
    }

    private void usersAreFoundByCriteria(Collection<User> actual, User... users) {
        List<User> actualResult = new ArrayList<>(actual);
        resetIds(actualResult);
        List<User> expectedResult = Arrays.asList(users);
        Assertions.assertEquals(actualResult, expectedResult);
    }

    private void resetIds(Collection<User> users) {
        users.forEach(it -> it.setId(0L));
    }

}
