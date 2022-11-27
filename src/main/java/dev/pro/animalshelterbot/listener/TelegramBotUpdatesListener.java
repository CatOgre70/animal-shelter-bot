package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.constants.Constants;
import dev.pro.animalshelterbot.KeyboardFactory;
import dev.pro.animalshelterbot.constants.Commands;
import dev.pro.animalshelterbot.service.ChatConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.zip.DataFormatException;


// Уже есть пара работающих методов
// Проработать вариант со switch и enums
// Прописать применение остальных команд
// Продумать дальнейшую логику

/**
 * The main service of the bot containing the logic of processing incoming updates
 */

//@Service
//public class TelegramBotUpdatesListener implements UpdatesListener {
//
//    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
//
//    private final TelegramBot telegramBot;
//
//    private final ChatConfigService chatConfigService;
//
////    private final UserService userService;
//
//    private List<User> userDb;
//
//    public TelegramBotUpdatesListener(TelegramBot telegramBot, ChatConfigService chatConfigService, UserService userService) {
//        this.telegramBot = telegramBot;
//        this.chatConfigService = chatConfigService;
//
//    }
//
//    @PostConstruct
//    public void init() {
//        telegramBot.setUpdatesListener(this);
//    }
//
//
//    @Override
//    public int process(List<Update> updates) {
//        updates.forEach(update -> {
//            logger.info("Processing update: {}", update);
//            // Process your updates here
//            Long chatId = update.message().chat().id();
//            String incomingMessage = update.message().text();
//
//            if (chatId.equals(userDb.) )
//
//            if (incomingMessage.equalsIgnoreCase(String.valueOf(Commands.START))) {
//                SendResponse response = telegramBot.execute(new SendMessage(chatId, Constants.START_DESCRIPTION));
//                answer(response);
//            } else {
//                if (incomingMessage.equalsIgnoreCase(String.valueOf(Commands.MENU))){
//                    SendMessage message = new SendMessage(update.message().chat().id(), Constants.CHOOSE_OPTION);
//                    message.replyMarkup(KeyboardFactory.startButtons());
//                    SendResponse response = telegramBot.execute(message);
//
//
////              try {
////                    createNotification(update);
////                    SendResponse response = telegramBot.execute(new SendMessage(chatId, "Your notification saved"));
////                    answer(response);
////                    logger.info("Notification saved");
////                } catch (DataFormatException e) {
////                    logger.warn("Notification unsaved");
////                    SendResponse response = telegramBot.execute
////                            (new SendMessage(chatId,
////                                    "Incorrect notification text. " +
////                                            "Enter the notification in the format <dd.MM.yyyy HH:mm Notification text> without quotes and brackets"));
////                    answer(response);
////                }
////            }
//
////        });
////        finally{
////            {
//            return UpdatesListener.CONFIRMED_UPDATES_ALL;
//            }
//        }
//
//    private void answer(SendResponse response) {
//        if (!response.isOk()) {
//            logger.warn("Response error code is: {}", response.errorCode());
//        } else {
//            logger.info("Response is: {}", response.isOk());
//        }
//    }
//
//    public void requestDefinition() {
//
//    }
//
//    public List<String> requestParsing(String text) throws DataFormatException {
//        Matcher matcher = pattern.matcher(text);
//        if (matcher.matches()) {
//            String dateTime = matcher.group(1);
//            String notification = matcher.group(3);
//            return List.of(dateTime, notification);
//        } else {
//            logger.warn("Incorrect data format");
//            throw new DataFormatException("Incorrect data format");
//        }
//    }
//
//    public void createNotification(Update update) throws DataFormatException {
//        String message = update.message().text();
//        Long chatId = update.message().chat().id();
//        List<String> timeAndText = new ArrayList<>(requestParsing(message));
//        String time = timeAndText.get(0);
//        String text = timeAndText.get(1);
//        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
//        NotificationTask notificationTask = new NotificationTask(chatId, text, localDateTime);
//        notificationTaskRepository.save(notificationTask);
//        logger.info("Notification save {}", notificationTask);
//    }
//}
