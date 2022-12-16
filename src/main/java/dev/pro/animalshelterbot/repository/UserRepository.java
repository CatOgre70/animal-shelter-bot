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

    @Query(value = "SELECT * FROM users WHERE (users.first_name ILIKE CONCAT('%',:firstNameSub,'%') " +
            "AND users.second_name ILIKE CONCAT('%',:secondNameSub,'%') " +
            "AND users.nick_name ILIKE CONCAT('%',:nickNameSub,'%'))", nativeQuery = true)
    public List<User> findByThreeSubstrings(String firstNameSub, String secondNameSub, String nickNameSub);

    Optional<User> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
