package dev.pro.animalshelterbot.service;

import dev.pro.animalshelterbot.exception.AdoptedAnimalNotFoundException;
import dev.pro.animalshelterbot.exception.DailyReportNotFoundException;
import dev.pro.animalshelterbot.exception.UserNotFoundException;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.repository.DailyReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Transactional
@Service
public class DailyReportService {

    @Value("dailyreportphotos")
    private String photosDir;

    private final DailyReportRepository dailyReportRepository;

    private final ChatConfigService chatConfigService;

    private final UserService userService;

    public DailyReportService(DailyReportRepository dailyReportRepository, ChatConfigService chatConfigService,
                                UserService userService) {
        this.dailyReportRepository = dailyReportRepository;
        this.chatConfigService = chatConfigService;
        this.userService = userService;
    }
    /**
     * event recording process
     */
    private final Logger logger = LoggerFactory.getLogger(DailyReportRepository.class);

    /**
     * saving the animal in the database
     * the repository method is used {@link JpaRepository#save(Object)}
     * event recording process
     * param dailyReport, must not be null
     * return the user stored in the database
     */
    public DailyReport addDailyReport(DailyReport dailyReport) {
        logger.info("Method \"UserService.addDailyReport()\" was called");
        return dailyReportRepository.save(dailyReport);
    }
    /**
     * find for an dailyReport by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * param id user, must not be null
     * return found user
     */
    public DailyReport findDailyReport(long id) {
        logger.info("Method \"UserService.findDailyReport()\" was called");
        return dailyReportRepository.findById(id).orElse(null);
    }
    /**
     * edit for an dailyReport by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * fetching data from the database and modifying it
     * param dailyReport report with new data should be saved in the database
     * return DailyReport object saved in the database
     */
    public Optional<DailyReport> editDailyReport(DailyReport dailyReport) {
        logger.info("Method \"DailyReportService.DailyReport()\" was called");
        Optional<DailyReport> optional = dailyReportRepository.findById(dailyReport.getId());
        if(optional.isEmpty()) {
            return Optional.empty();
        } else {
            DailyReport fromDb = optional.get();
            fromDb.setDateTime(dailyReport.getDateTime());
            fromDb.setFilePath(dailyReport.getFilePath());
            fromDb.setDiet(dailyReport.getDiet());
            fromDb.setGeneralWellBeing(dailyReport.getGeneralWellBeing());
            fromDb.setChangeInBehavior(dailyReport.getChangeInBehavior());
            return Optional.of(dailyReportRepository.save(fromDb));
        }
    }
    /**
     * delete for an dailyReport by ID in the database
     * the repository method is used {@link JpaRepository#deleteById(Object)}
     * event recording process
     * param id, must not be null
     */
    public Optional<DailyReport> deleteDailyReport(long id) {
        logger.info("Method \"UserService.deleteDailyReport()\" was called");
        Optional<DailyReport> result = dailyReportRepository.findById(id);
        if(result.isPresent()) {
            dailyReportRepository.deleteById(id);
        }
        return result;
    }
    /**
     * upload photo by id
     * event recording process
     * the exception method is used
     * setting the file size
     * save photo in dailyReport
     */
    public void uploadPhoto(Long id, MultipartFile photo) throws IOException {
        logger.info("Method \"AnimalService.uploadPhoto()\" was invoked");

        DailyReport dailyReport = dailyReportRepository.findById(id).orElseThrow(
                () -> new DailyReportNotFoundException("There is no animal with such id in the database"));

        Path filePath = Path.of(photosDir, id + "." + getExtensions(photo.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (InputStream is = photo.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        dailyReport.setFilePath(filePath.toString());
        dailyReport.setFileSize(photo.getSize());
        dailyReport.setMediaType(photo.getContentType());
        try {
            dailyReport.setSmallPicture(generateImagePreview(filePath));
        } catch(IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        dailyReportRepository.save(dailyReport);
    }

    public void downloadAndUploadPhoto(Long id, String fileFullPath, Long fileSize, String mediaType) throws IOException {
        logger.info("Method \"AnimalService.downloadAndUploadPhoto()\" was invoked");
        DailyReport dailyReport = dailyReportRepository.findById(id).orElseThrow(
                () -> new DailyReportNotFoundException("There is no animal with such id in the database"));
        BufferedInputStream bis = new BufferedInputStream(new URL(fileFullPath).openStream(), 1024);
        Path outputFilePath = Path.of(photosDir, id + "." + getExtensions(fileFullPath));
        Files.createDirectories(outputFilePath.getParent());
        Files.deleteIfExists(outputFilePath);
        OutputStream os = Files.newOutputStream(outputFilePath, CREATE_NEW);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        bis.transferTo(bos);
        dailyReport.setFilePath(outputFilePath.toString());
        dailyReport.setFileSize(fileSize);
        dailyReport.setMediaType(mediaType);
        try {
            dailyReport.setSmallPicture(generateImagePreview(outputFilePath));
        } catch(IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        dailyReportRepository.save(dailyReport);
    }

    /**
     * Generate photo preview for the database
     * param filePath - path to the avatar file
     * return image with smaller size as byte[]
     * throws IOException well, shit happens sometimes
     */
    private byte[] generateImagePreview(Path filePath) throws IOException {
        logger.debug("Method \"DailyReportService.generateImagePreview()\" was invoked with Path parameter: " + filePath);
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
     * param fileName - file name
     * return file extension
     */
    private String getExtensions(String fileName) {
        logger.debug("Method \"DailyReportService.getExtensions()\" was invoked with String parameter: " + fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Find daily report from chatId
     * @param chatId
     * UserNotFoundException - user not found in database
     * AdoptedAnimalNotFoundException - the animal does not have a guardian
     * @return daily report
     */
    public Optional<DailyReport> findDailyReportByChatId(Long chatId) {
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        Optional<User> result = userService.findByChatId(chatId);
        Animal animal;
        if(result.isEmpty()) {
            throw new UserNotFoundException("User with such chatId was not found in the database");
        }
        if(result.get().getAdoptedAnimal() != null) {
            animal = result.get().getAdoptedAnimal();
        } else {
            throw new AdoptedAnimalNotFoundException("User with such chatId has not any adopted animals");
        }
        List<DailyReport> dailyReports = dailyReportRepository.getDailyReportByAnimalId(animal.getId());
        for(DailyReport dr : dailyReports){
            if(dr.getDateTime().truncatedTo(ChronoUnit.DAYS).isEqual(localDateTime))
                return Optional.of(dr);
        }
        return Optional.empty();
    }
}
