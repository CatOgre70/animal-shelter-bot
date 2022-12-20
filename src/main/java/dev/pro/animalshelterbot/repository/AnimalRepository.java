package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.constants.AnimalKind;
import dev.pro.animalshelterbot.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AnimalRepository is the interface for storing Animal Shelter animals information
 * Corresponds to the animals table in PostgreSQL. Extends {@link JpaRepository}
 * @see Animal Animal
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query(value = "SELECT * FROM animals WHERE (animals.name ILIKE CONCAT('%',:nameSub,'%') " +
            "AND animals.breed ILIKE CONCAT('%',:breedSub,'%') " +
            "AND animals.color ILIKE CONCAT('%',:colorSub,'%'))", nativeQuery = true)
    List<Animal> getAnimalsBySubstrings(String nameSub, String breedSub, String colorSub);

    @Query(value = "SELECT * FROM animals WHERE (animals.name ILIKE CONCAT('%',:nameSub,'%') " +
            "AND animals.kind = :animalKind " +
            "AND animals.breed ILIKE CONCAT('%',:breedSub,'%') " +
            "AND animals.color ILIKE CONCAT('%',:colorSub,'%'))", nativeQuery = true)
    List<Animal> getAnimalBySubstringsAndKind(String nameSub, int animalKind, String breedSub, String colorSub);

}
