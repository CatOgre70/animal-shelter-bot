package dev.pro.animalshelterbot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents animal of the shelter. Animal class corresponds to the @Entity(name = "animals") in PostgreSQL.
 * Model for the AnimalsRepository interface
 */
@Entity(name = "animals")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long animalId;

    private String name;

    private String kind;

    private String breed;

    private String color;

    private String features;

    private String filePath;

    private int fileSize;

    private byte[] avatarPicture;

    private LocalDateTime adoptionDate;

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "dailyReportId")
    List<DailyReport> dailyReports;

    public Animal() {
        this.name = null;
        this.kind = null;
        this.breed = null;
        this.color = null;
        this.features = null;
        this.filePath = null;
        this.fileSize = 0;
        this.avatarPicture = null;
        this.adoptionDate = null;
    }

    public Animal(String name, String kind, String breed, String color, String features, String filePath,
                  int fileSize, byte[] avatarPicture, LocalDateTime adoptionDate) {
        this.name = name;
        this.kind = kind;
        this.breed = breed;
        this.color = color;
        this.features = features;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.avatarPicture = avatarPicture;
        this.adoptionDate = adoptionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getAvatarPicture() {
        return avatarPicture;
    }

    public void setAvatarPicture(byte[] avatarPicture) {
        this.avatarPicture = avatarPicture;
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

    public List<DailyReport> getReports() {
        return dailyReports;
    }

    public void setReports(List<DailyReport> dailyReports) {
        this.dailyReports = dailyReports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(animalId, animal.animalId) && Objects.equals(name, animal.name) && Objects.equals(kind, animal.kind) && Objects.equals(breed, animal.breed) && Objects.equals(color, animal.color) && Objects.equals(features, animal.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(animalId, name, kind, breed, color, features);
    }

    @ManyToOne(optional = false)
    private User users;

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }
}