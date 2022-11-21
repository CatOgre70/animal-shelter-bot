package dev.pro.animalshelterbot.service;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.repository.AnimalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * event recording process
     */
    private final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    /**
     * saving the animal in the database
     * the repository method is used {@link JpaRepository#save(Object)}
     * event recording process
     * @param animal, must not be null
     * @return the animal stored in the database
     */
    public Animal addAnimal(Animal animal) {
        logger.info("Metod \"UserService.addAnimal()\" was called");
        return animalRepository.save(animal);
    }
    /**
     * find for an animal by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * @param id animal, must not be null
     * @return found animal
     */
    public Animal findAnimal(long id) {
        logger.info("Metod \"UserService.findAnimal()\" was called");
        return animalRepository.findById(id).orElse(null);
    }
    /**
     * edit for an animal by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * fetching data from the database and modifying it
     * @param animal
     * @return making changes to the database
     */
    public Animal editAnimal(Animal animal) {
        logger.info("Metod \"AnimalService.editAnimal()\" was called");
        Optional<Animal> optional = animalRepository.findById(animal.getAnimalId());
        if(!optional.isPresent()) {
            return null;
        }
        else {
            Animal fromDb = optional.get();
            fromDb.setReports(animal.getReports());
            fromDb.setAdoptionDate(animal.getAdoptionDate());
            fromDb.setAdoptionDate(animal.getAdoptionDate());
            fromDb.setOwner(animal.getOwner());
            return animalRepository.save(fromDb);
        }
    }
    /**
     * delete for an animal by ID in the database
     * the repository method is used {@link JpaRepository#deleteById(Object)}
     * event recording process
     * @param id, must not be null
     */
    public void deleteAnimal(long id) {
        logger.info("Metod \"UserService.deleteAnimal()\" was called");
        animalRepository.deleteById(id);
    }

}
