package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.repository.DailyReportRepository;
import dev.pro.animalshelterbot.service.AnimalService;
import dev.pro.animalshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * This class used to send daily notifications to the user within 30 days from animal adoption
 * date or if the deadline for sending the report has passed
 */

@Transactional
@Service
public class Scheduler {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserService userService;
    private final AnimalService animalService;
    private final TelegramBot telegramBot;

    private final DailyReportRepository dailyReportRepository;

    public Scheduler(UserService userService, AnimalService animalService, TelegramBot telegramBot, DailyReportRepository dailyReportRepository) {
        this.userService = userService;
        this.animalService = animalService;
        this.telegramBot = telegramBot;
        this.dailyReportRepository = dailyReportRepository;
    }


    @Scheduled(cron = "0 0 11 * * *" )
    public void sendNotification() {
        List<Animal> adoptedAnimals = animalService.getAllAdoptedAnimals();
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        List<LocalDate> lastReportDate = new ArrayList<>();
        for(int i = 0; i < adoptedAnimals.size(); i++) {
            List<DailyReport> dailyReports = dailyReportRepository.getDailyReportByAnimalId(adoptedAnimals.get(i).getId());
            LocalDate lastReportDateTemp = adoptedAnimals.get(i).getAdoptionDate().toLocalDate();
            for(int j = 0; j < dailyReports.size(); j++) {
                if(lastReportDateTemp.isBefore(dailyReports.get(i).getDateTime().toLocalDate())){
                    lastReportDateTemp = dailyReports.get(i).getDateTime().toLocalDate();
                }
            }
            lastReportDate.add(lastReportDateTemp);
        }
        for(int i = 0; i < adoptedAnimals.size(); i++) {
            LocalDateTime adoptionDate = adoptedAnimals.get(i).getAdoptionDate();
            if(today.isBefore(adoptionDate.plusDays(30L))) {
                LocalDate adoptionDate1 = adoptionDate.toLocalDate();
                LocalDate today1 = today.toLocalDate();
                Period period = Period.between(lastReportDate.get(i), today1);
                if(period.getDays() > 2) {
                    sendMessage(adoptedAnimals.get(i).getOwner().getChatId(), "Прошло больше двух дней с момента последнего отчета! Вы нарушили договор об адаптации животного! Я вызываю волонтеров!");
                } else {
                    period = Period.between(today1, adoptionDate1);
                    sendMessage(adoptedAnimals.get(i).getOwner().getChatId(), "Прошло дней: " + period.getDays() + ". Новый день - новый отчет!");
                }
            }
        }
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(sendMessage);
        if (response != null && response.isOk()) {
            logger.info("message: {} is sent ", message);
        } else if(response != null) {
            logger.warn("Message was not sent. Error code:  " + response.errorCode());
        } else {
            logger.warn("Message was not sent. Response is null");
        }
    }
}
