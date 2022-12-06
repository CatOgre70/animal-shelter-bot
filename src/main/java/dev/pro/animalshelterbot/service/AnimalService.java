package dev.pro.animalshelterbot.service;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.repository.AnimalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AnimalService {

    @Value("/avatars")
    private String avatarsDir;

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
//     * @param animal, must not be null
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
        Optional<Animal> optional = animalRepository.findById(animal.getId());
        if(!optional.isPresent()) {
            return null;
        }
        else {
            Animal fromDb = optional.get();
            fromDb.setReports(animal.getReports());
            fromDb.setAdoptionDate(animal.getAdoptionDate());
            fromDb.setFeatures(animal.getFeatures());
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
    /**
     * find for an animal by name in the database
     * the repository method is used {@link JpaRepository#(Object)}
     * event recording process
     * @param name animal, must not be null
     * @return found animal
     */
    public Collection <Animal> getName(String name) {
        logger.info("Metod \"UserService.getName()\" was called");
        return animalRepository.getName(name);
    }
    /**
     * find for an animal by ID in the database
     * the repository method is used {@link JpaRepository#(Object)}
     * event recording process
     * @param kind animal, must not be null
     * @return found animal
     */
    public Collection <Animal> getKind(String kind) {
        logger.info("Metod \"UserService.getKind()\" was called");
        return animalRepository.getKind(kind);
    }
    /**
     * find for an animal by ID in the database
     * the repository method is used {@link JpaRepository#(Object)}
     * event recording process
     * @param breed animal, must not be null
     * @return found animal
     */
    public Collection <Animal> getBreed(String breed) {
        logger.info("Metod \"UserService.getBreed()\" was called");
        return animalRepository.getBreed(breed);
    }
    /**
     * find for an animalin the database
     * the repository method is used {@link JpaRepository#(Object)()}
     * event recording process
     * @return found animal
     */
    public Collection <Animal> getAllAnimal() {
        logger.info("Metod \"UserService.getAllAnimal()\" was called");
        return animalRepository.findAll();
    }

    public Animal findAvatar(long id) {
        return animalRepository.findById(id).orElseThrow();
    }
    public void uploadAvatar(Long id, MultipartFile file) throws IOException {
        Path filePath = Path.of(avatarsDir, id + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
        Animal animal = animalRepository.findById(id).orElseGet(Animal::new);
        animal.setFilePath(filePath.toString());
        animal.setFileSize(file.getSize());
        animal.setMediaType(file.getContentType());
        animal.setAvatarPicture(file.getBytes());
        animalRepository.save(animal);
    }
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Collection<Animal> getAvatarPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return animalRepository.findAll(pageRequest).getContent();
    }

    public Collection<Animal> getAnimalBySubstrings(String name, String kind, String breed, String color) {
        return animalRepository.getAnimalsBySubstrings(name, kind, breed, color);
    }

    public Optional<Animal> getAnimalById(Long id) {
        return animalRepository.findById(id);
    }
}
