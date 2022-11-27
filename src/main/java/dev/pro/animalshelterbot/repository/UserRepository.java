package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * UserRepository is the interface for storing Animal Shelter users (clients) information
 * Corresponds to the users table in PostgreSQL. Extends {@link JpaRepository}
 * @see User User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT  (first_name) FROM users", nativeQuery = true)
    Collection<User> findByFirstName(String firstName);

    @Query(value = "SELECT  (second_name) FROM users", nativeQuery = true)
    Collection<User> findBySecondName(String secondName);

    @Query(value = "SELECT  (nick_name) FROM users", nativeQuery = true)
    Collection<User> findByNickName(String nickName);
}
