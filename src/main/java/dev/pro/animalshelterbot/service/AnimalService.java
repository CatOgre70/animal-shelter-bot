package dev.pro.animalshelterbot.service;
import dev.pro.animalshelterbot.exception.AnimalNotFoundException;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.repository.AnimalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AnimalService {

    @Value("avatars")
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
        logger.info("Method \"UserService.addAnimal()\" was called");
        return animalRepository.save(animal);
    }

    /**
     * edit for an animal by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * fetching data from the database and modifying it
     * @param animal Animal class object to be the source of data for saving in the database
     * @return Animal class object with result of saving changes in the database
     */
    public Animal editAnimal(Animal animal) {
        logger.info("Method \"AnimalService.editAnimal()\" was called");
        Optional<Animal> optional = animalRepository.findById(animal.getId());
        if(optional.isEmpty()) {
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
        logger.info("Method \"UserService.deleteAnimal()\" was called");
        animalRepository.deleteById(id);
    }

    /**
     * Find animals by substrings in their name, kind, breed, color
     * @param name - substring of Animal name we are looking for
     * @param kind - substring of Animal kind we are looking for
     * @param breed - substring of Animal breed we are looking for
     * @param color - substring of Animal color we are looking for
     * @return - List of Animals which name, kind, breed and color contain selected substrings
     */
    public List<Animal> getAnimalBySubstrings(String name, String kind, String breed, String color) {
        return animalRepository.getAnimalsBySubstrings(name, kind, breed, color);
    }

    /**
     * Find an animal by id
     * @param id - Animal id
     * @return Animal found by id
     */
    public Optional<Animal> getAnimalById(Long id) {
        return animalRepository.findById(id);
    }

    /**
     * Find for an animal in the database
     * the repository method is used {@link JpaRepository#(Object)()}
     * event recording process
     * @return found animal
     */
    public Collection <Animal> getAllAnimals() {
        logger.info("Method \"UserService.getAllAnimal()\" was called");
        return animalRepository.findAll();
    }

    /**
     * Upload the avatar file/picture to the root application directory and 'avatars' subdirectory
     * also prepare avatar preview and put it into the database
     * @param id - Animal id
     * @param file - file with avatar picture
     * @throws IOException well, shit happens sometime :)
     */
    public void uploadAvatar(Long id, MultipartFile file) throws IOException {
        logger.info("Method \"AnimalService.uploadAvatar()\" was invoked");

        Animal animal = animalRepository.findById(id).orElseThrow(
                () -> new AnimalNotFoundException("There is no animal with such id in the database"));

        Path filePath = Path.of(avatarsDir, id + "." + getExtensions(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        animal.setFilePath(filePath.toString());
        animal.setFileSize(file.getSize());
        animal.setMediaType(file.getContentType());
        try {
            animal.setAvatarPreview(generateImagePreview(filePath));
        } catch(IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        animalRepository.save(animal);
    }

    /**
     * Generate avatar preview for the database
     * @param filePath - path to the avatar file
     * @return image with smaller size as byte[]
     * @throws IOException well, shit happens sometimes
     */
    private byte[] generateImagePreview(Path filePath) throws IOException {
        logger.debug("Method \"AnimalService.generateImagePreview()\" was invoked with Path parameter: " + filePath);
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ){
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtensions(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    /**
     * Get extension from fileName
     * @param fileName - file name
     * @return file extension
     */
    private String getExtensions(String fileName) {
        logger.debug("Method \"AvatarService.getExtensions()\" was invoked with String parameter: " + fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


}
