package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * AnimalRepository is the interface for storing Animal Shelter animals information
 * Corresponds to the animals table in PostgreSQL. Extends {@link JpaRepository}
 * @see Animal Animal
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional <Animal> findById(Long id);

    @Query(value = "SELECT (name) FROM animals", nativeQuery = true)
    Collection <Animal> getName(String name);

    @Query(value = "SELECT (kind) FROM animals", nativeQuery = true)
    Collection <Animal> getKind(String kind);

    @Query(value = "SELECT (breed) FROM animals", nativeQuery = true)
    Collection <Animal> getBreed(String breed);

}
