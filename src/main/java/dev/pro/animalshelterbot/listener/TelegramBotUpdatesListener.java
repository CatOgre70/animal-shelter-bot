package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.constants.BotStatus;
import dev.pro.animalshelterbot.constants.Constants;
import dev.pro.animalshelterbot.factory.KeyboardFactory;
import dev.pro.animalshelterbot.constants.Commands;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The main service of the bot containing the logic of processing incoming updates
 */

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ChatConfigService chatConfigService;

    private final UserService userService;

    private List<User> userDb;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, ChatConfigService chatConfigService, UserService userService) {
        this.telegramBot = telegramBot;
        this.chatConfigService = chatConfigService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
                    logger.info("Processing update: {}", update);

                    // Process your updates here

                    if (update.message().text() != null) {
                        String incomingMessage = update.message().text();
                        Long chatId = update.message().chat().id();
                        if (!userService.checkByChatId(chatId)) {
                            SendMessage message = new SendMessage(chatId, Constants.REQUEST_START);
                            SendResponse response = telegramBot.execute(message);
                            if (incomingMessage.equalsIgnoreCase(String.valueOf(Commands.START))) {
                                userService.addUser(new dev.pro.animalshelterbot.model.User());
                                chatConfigService.addChatConfig(new ChatConfig(chatId, 0L));
                                SendMessage sendMessage = new SendMessage(chatId, Constants.START_DESCRIPTION);
                                SendResponse sendResponse = telegramBot.execute(sendMessage);
                                SendMessage keys = new SendMessage(update.message().chat().id(), Constants.CHOOSE_OPTION);
                                message.replyMarkup(KeyboardFactory.startButtons());
                                SendResponse response1 = telegramBot.execute(keys);
                            } else if (!incomingMessage.equalsIgnoreCase(String.valueOf(Commands.START))) {
                                SendMessage sendMessage = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
                                SendResponse sendResponse = telegramBot.execute(message);
                            }

                        } else {
                            statusDeterminant(chatId);
                        }


                    } else if (update.message().photo() != null) {
                        PhotoSize[] incomingMessage = update.message().photo();
                        Long chatId = update.message().chat().id();
                        if (!userService.checkByChatId(chatId)) {
                            SendMessage message = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
                            SendResponse response = telegramBot.execute(message);
                        } else if (chatConfigService.checkByChatId(3L)) {
                            // проверка наличия текста DailyReport, если есть, то ок, если нет - запрос
                        } else {
                            SendMessage message = new SendMessage(chatId, "У Вас нет животного на адаптации, вы не можете направить фотоотчёт." + Constants.CHOOSE_OPTION + Commands.MENU);
                            SendResponse response = telegramBot.execute(message);
                        }
                    } else if (update.callbackQuery() != null) { // Callback answer processing
                        Long chatId = update.message().chat().id();
                        SendMessage message = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
                        SendResponse response = telegramBot.execute(message);

                    }

                });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SendResponse statusDeterminant(Long chatId) {
        BotStatus botStatus = chatConfigService.findByChatId(chatId);
        SendMessage message = null;
        SendResponse response = telegramBot.execute(message);
        switch (botStatus) {
            case DEFAULT:
                message = new SendMessage(chatId, Constants.CHOOSE_OPTION);
                message.replyMarkup(KeyboardFactory.startButtons());
                response = telegramBot.execute(message);
                break;
            case CONSULT_NEW_USER:
                message = new SendMessage(chatId, Constants.CHOOSE_OPTION);
                message.replyMarkup(KeyboardFactory.stageOne());
                response = telegramBot.execute(message);
                break;
            case CONSULT_POTENTIAL_OWNER:
                message = new SendMessage(chatId, Constants.CHOOSE_OPTION);
                message.replyMarkup(KeyboardFactory.stageTwo());
                response = telegramBot.execute(message);
                break;
                // Не реализованы кнопки
            case KEEPING_a_PET:
                message = new SendMessage(chatId, Constants.KEEPING_a_PET);
                response = telegramBot.execute(message);
                break;
            // Не реализовано
            case CHAT_WITH_VOLUNTEER:
                message = new SendMessage(chatId, Constants.CALL_VOLUNTEER);
                response = telegramBot.execute(message);
                break;
            default:
                Exception exception; // не знаю нужно ли цеплять
        }
        return response;
    }

    private void answer(SendResponse response) {
        if (!response.isOk()) {
            logger.warn("Response error code is: {}", response.errorCode());
        } else {
            logger.info("Response is: {}", response.isOk());
        }
    }
}




//                if (incomingMessage.equalsIgnoreCase(String.valueOf(Commands.MENU))) {
//                    SendMessage message = new SendMessage(update.message().chat().id(), Constants.CHOOSE_OPTION);
//                    message.replyMarkup(KeyboardFactory.startButtons());
//                    SendResponse response = telegramBot.execute(message);
//                }

//                if (incomingMessage.equalsIgnoreCase(String.valueOf(Commands.HELP))) {
//                    SendMessage message = new SendMessage(update.message().chat().id(), Arrays.toString(Commands.values()));
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
