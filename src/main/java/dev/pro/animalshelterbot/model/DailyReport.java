package dev.pro.animalshelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


/**
 * Represents daily report about adopted animal. DailyReport class corresponds to the @Entity(name = "daily_reports") in PostgreSQL.
 * Model for the DailyReportsRepository interface
 */
@Entity
@Table(name = "daily_reports")
public class DailyReport {

    /**
     * dailyReportId - DailyReport identifier, primary key of the daily_reports table in PostgreSQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date and time of the last report update
     */
    private LocalDateTime dateTime;

    /**
     * Relative path to the animal photo file for DailyReport record/class
     */
    private String filePath;

    /**
     * Size of the animal photo file in bytes
     */
    private Long fileSize;

    private String mediaType;

    /**
     * Animal photo file thumbnail
     */
    private byte[] smallPicture;

    /**
     * Description of the current day food consumption and/or plan
     */
    private String diet;

    /**
     * Description of the animal general well-being
     */
    private String generalWellBeing;

    /**
     * Description of the animal changes in behavior
     */
    private String changeInBehavior;

    /**
     * Daily report is about this animal
     */
    private Long animalId;

    /**
     * DailyReport class empty constructor for Spring JPA and Hibernate
     */
    public DailyReport() {
        this.id = 0L;
        this.dateTime = null;
        this.filePath = null;
        this.fileSize = 0L;
        this.mediaType = null;
        this.smallPicture = null;
        this.diet = null;
        this.generalWellBeing = null;
        this.changeInBehavior = null;
        this.animalId = null;
    }

    /**
     * DailyReport class constructor for using in the AnimalShelterBotApplication
     */
    public DailyReport(LocalDateTime dateTime, String filePath, Long fileSize, String mediaType, byte[] smallPicture,
                       String diet, String generalWellBeing, String changeInBehavior, Long animalId) {
        this.id = 0L;
        this.dateTime = dateTime;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.smallPicture = smallPicture;
        this.diet = diet;
        this.generalWellBeing = generalWellBeing;
        this.changeInBehavior = changeInBehavior;
        this.animalId = animalId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long id) { this.animalId = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyReport report = (DailyReport) o;
        return Objects.equals(fileSize, report.fileSize) && Objects.equals(id, report.id) &&
                Objects.equals(dateTime, report.dateTime) && Objects.equals(filePath, report.filePath) &&
                Arrays.equals(smallPicture, report.smallPicture) && Objects.equals(diet, report.diet) &&
                Objects.equals(generalWellBeing, report.generalWellBeing) &&
                Objects.equals(changeInBehavior, report.changeInBehavior) &&
                (animalId == report.getAnimalId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, dateTime, filePath, fileSize, diet, generalWellBeing, changeInBehavior, animalId);
        result = 31 * result + Arrays.hashCode(smallPicture);
        return result;
    }
}
