package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Продумать реализацию напоминаний и переписать метод класса
/**
 * This class used to send messages to the user if the deadline for sending the report has passed
 */


public class Sheduler {
//    @Scheduled(cron = "0 0/1 * * * *")
//    public void sendNotification() {
//        List<NotificationTask> sentNotifications = notificationTaskRepository
//                .findAllByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
//        sentNotifications.forEach(notificationTask -> {
//            long chatId = notificationTask.getChatId();
//            String message = notificationTask.getNotification();
//            if (!message.isEmpty()) {
//                SendResponse sendNotification = telegramBot.execute(new SendMessage(chatId,
//                        "new notification - " + message));
//                logger.info("notification - {}, chatId - {}", message, chatId);
//                answer(sendNotification);
//            }
//        });
//    }
}
