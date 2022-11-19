package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepository is the interface for storing Animal Shelter users (clients) information
 * Corresponds to the users table in PostgreSQL. Extends {@link JpaRepository}
 * @see User User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
