package model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


/**
 * Represents daily report about adopted animal. DailyReport class corresponds to the @Entity(name = "daily_reports") in PostgreSQL.
 * Model for the DailyReportsRepository interface
 */
@Entity(name = "daily_reports")
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    private String filePath;

    private int fileSize;

    private byte[] smallPicture;

    private String diet;

    private String generalWellBeing;

    private String changeInBehavior;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public DailyReport() {
        this.dateTime = null;
        this.filePath = null;
        this.fileSize = 0;
        this.smallPicture = null;
        this.diet = null;
        this.generalWellBeing = null;
        this.changeInBehavior = null;
    }

    public DailyReport(LocalDateTime dateTime, String filePath, int fileSize, byte[] smallPicture, String diet, String generalWellBeing, String changeInBehavior) {
        this.dateTime = dateTime;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.smallPicture = smallPicture;
        this.diet = diet;
        this.generalWellBeing = generalWellBeing;
        this.changeInBehavior = changeInBehavior;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public byte[] getSmallPicture() {
        return smallPicture;
    }

    public void setSmallPicture(byte[] smallPicture) {
        this.smallPicture = smallPicture;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getGeneralWellBeing() {
        return generalWellBeing;
    }

    public void setGeneralWellBeing(String generalWellBeing) {
        this.generalWellBeing = generalWellBeing;
    }

    public String getChangeInBehavior() {
        return changeInBehavior;
    }

    public void setChangeInBehavior(String changeInBehavior) {
        this.changeInBehavior = changeInBehavior;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyReport report = (DailyReport) o;
        return fileSize == report.fileSize && Objects.equals(id, report.id) && Objects.equals(dateTime, report.dateTime) && Objects.equals(filePath, report.filePath) && Arrays.equals(smallPicture, report.smallPicture) && Objects.equals(diet, report.diet) && Objects.equals(generalWellBeing, report.generalWellBeing) && Objects.equals(changeInBehavior, report.changeInBehavior) && Objects.equals(animal, report.animal);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, dateTime, filePath, fileSize, diet, generalWellBeing, changeInBehavior, animal);
        result = 31 * result + Arrays.hashCode(smallPicture);
        return result;
    }
}