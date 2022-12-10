package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * UserRepository is the interface for storing Animal Shelter users (clients) information
 * Corresponds to the users table in PostgreSQL. Extends {@link JpaRepository}
 * @see User User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Collection<User> findByFirstNameContainsIgnoreCaseAndSecondNameContainsIgnoreCaseAndNickNameContainsIgnoreCase(
            String firstNameSubstring,
            String lastNameSubstring,
            String nickNameSubstring);

    Collection<User> findByFirstNameContainsIgnoreCase(String firstNameSubstring);
    Collection<User> findBySecondNameContainsIgnoreCase(String secondNameSubstring);
    Collection<User> findByNickNameContainsIgnoreCase(String nickNameSubstring);
    Collection<User> findByFirstNameContainsIgnoreCaseAndSecondNameContainsIgnoreCase(String firstNameSubstring,
                                                                                      String secondNameSubstring);
    Collection<User> findByFirstNameContainsIgnoreCaseAndNickNameContainsIgnoreCase(String firstNameSubstring,
                                                                                    String nickNameSubstring);
    Collection<User> findBySecondNameContainsIgnoreCaseAndNickNameContainsIgnoreCase(String secondNameSubstring,
                                                                                       String nickNameSubstring);

    @Query(value = "SELECT * FROM users WHERE (users.first_name ILIKE CONCAT('%',:firstNameSubstring,'%') " +
            "AND users.second_name ILIKE CONCAT('%',:secondNameSubstring,'%') " +
            "AND users.nick_name ILIKE CONCAT('%',:nickNameSubstring,'%'))", nativeQuery = true)
    Collection<User> findByThreeSubstrings(String firstNameSubstring, String secondNameSubstring, String nickNameSubstring);

    Collection<User> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
