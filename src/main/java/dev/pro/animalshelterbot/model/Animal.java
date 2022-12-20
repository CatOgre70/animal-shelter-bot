package dev.pro.animalshelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.pro.animalshelterbot.constants.AnimalKind;
import dev.pro.animalshelterbot.constants.Shelter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents animal of the shelter. Animal class corresponds to the @Entity(name = "animals") in PostgreSQL.
 * Model for the AnimalsRepository interface
 */
@Entity
@Table(name = "animals")
public class Animal {

    /**
     * animalId - Animal identifier, primary key of the animals table in PostgreSQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Animal name/nickname
     */
    private String name;

    /**
     * Animal kind (dog, cat, bird, snake...)
     */
    private AnimalKind kind;

    /**
     * Animal breed (dog breeds, for example: shepherd dog, bulldog, etc.)
     */
    private String breed;

    /**
     * Animal natural color, like color of fur, skin, ect.
     */
    private String color;

    /**
     * Animal features and peculiarities, like special food requirements, vaccinations, absence of limbs, illnesses, etc.
     */
    private String features;

    /**
     * Relative path to the animal photo file
     */
    private String filePath;

    /**
     * Size of the animal photo file in bytes
     */
    private Long fileSize;

    /**
     * Animal photo file thumbnail
     */
    private byte[] avatarPreview;

    private String mediaType;

    /**
     * Animal adoption date by the current owner
     */
    private LocalDateTime adoptionDate;

    /**
     * Animal owner
     */
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Shelter for the animal
     */
    private Shelter shelter;

    /**
     * Animal class empty constructor for Spring JPA and Hibernate
     */
    public Animal() {
        this.id = 0L;
        this.name = null;
        this.kind = null;
        this.breed = null;
        this.color = null;
        this.features = null;
        this.filePath = null;
        this.fileSize = 0L;
        this.avatarPreview = null;
        this.adoptionDate = null;
        this.mediaType = null;
        return;
    }

    /**
     * Animal class constructor for using in the AnimalShelterBotApplication
     */
    public Animal(String name, AnimalKind kind, String breed, String color, String features, String filePath,
                  Long fileSize, byte[] avatarPicture, LocalDateTime adoptionDate, String mediaType) {
        this.id = 0L;
        this.name = name;
        this.kind = kind;
        this.breed = breed;
        this.color = color;
        this.features = features;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.avatarPreview = avatarPicture;
        this.adoptionDate = adoptionDate;
        this.mediaType = mediaType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimalKind getKind() {
        return kind;
    }

    public void setKind(AnimalKind kind) {
        this.kind = kind;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getAvatarPreview() {
        return avatarPreview;
    }

    public void setAvatarPreview(byte[] avatarPicture) {
        this.avatarPreview = avatarPicture;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public LocalDateTime getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(LocalDateTime adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id) && Objects.equals(name, animal.name) && Objects.equals(kind, animal.kind) && Objects.equals(breed, animal.breed) && Objects.equals(color, animal.color) && Objects.equals(features, animal.features)
                && Objects.equals(fileSize, animal.fileSize) && Objects.equals(filePath, animal.filePath)
                && Objects.equals(mediaType, animal.mediaType) && Arrays.equals(avatarPreview, animal.avatarPreview);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, kind, breed, color, features, filePath, fileSize, mediaType);
        result = 31 * result + Arrays.hashCode(avatarPreview);
        return result;
    }

}