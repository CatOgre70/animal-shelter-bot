package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AnimalRepository is the interface for storing Animal Shelter animals information
 * Corresponds to the animals table in PostgreSQL. Extends {@link JpaRepository}
 * @see Animal Animal
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
