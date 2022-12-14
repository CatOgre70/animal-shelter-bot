package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.factory.KeyboardFactory;
import dev.pro.animalshelterbot.constants.*;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static dev.pro.animalshelterbot.constants.BotStatus.KEEPING_a_PET;

/**
 * The main service of the bot containing the logic of processing incoming updates
 */

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ChatConfigService chatConfigService;

    private final UserService userService;

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
            // Process your updates here
            logger.info("Processing update: {}", update);
            Long chatId = 0L;
            BotStatus botStatus;

            UpdateType updateType = checkingUpdate(update);
            if(updateType == UpdateType.COMMAND || updateType == UpdateType.MESSAGE || updateType == UpdateType.PHOTO) {
                chatId = update.message().chat().id();
            } else if(updateType == UpdateType.CALL_BACK_QUERY) {
                chatId = update.callbackQuery().from().id();
            }

            ChatConfig chatConfig = chatConfigService.findByChatId(chatId);

            if(chatConfig == null) { // New user
                chatConfig = new ChatConfig(chatId, BotStatus.DEFAULT.status);
                chatConfigService.addChatConfig(chatConfig);
                botStatus = BotStatus.NEW_USER;
            } else { // Not new user
                botStatus = BotStatus.valueOfStatus(chatConfig.getChatState());
            }

            switch (updateType) {
                case COMMAND:
                    processCommands(update, botStatus);
                    break;
                case MESSAGE:
//                    processMessages(update, botStatus);
                    break;
                case PHOTO:
//                    processPhotos(update, botStatus);
                    break;
                case CALL_BACK_QUERY:
//                    processCallbackQueries(update, botStatus);
                    break;
                case ERROR:
//                    processErrors(update, botStatus);
                    break;
            }

//            statusDeterminant(update.message().chat().id(), update);

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    private void processCommands(Update update, BotStatus botStatus) {
        Long chatId = update.message().chat().id();
        Commands command = Commands.valueOfCommandText(update.message().text());
        if(command == null) {
            sendMessage(chatId, Messages.THERE_IS_NO_SUCH_COMMAND.messageText);
            sendMessage(chatId, Messages.HELP.messageText);
            return;
        }
        if(command == Commands.START) {
            switch (botStatus) {
                case NEW_USER:
                    sendMessage(chatId, Messages.WELCOME_TO_THE_CHATBOT.messageText);
                    sendMessage(chatId, Messages.HELP.messageText);
                    break;
                case DEFAULT:
                case CONSULT_NEW_USER:
                case CONSULT_POTENTIAL_OWNER:
                case KEEPING_a_PET:
                case CHAT_WITH_VOLUNTEER:
                    sendMessage(chatId, Messages.HELP.messageText);
            }
        } else if(command == Commands.HELP) {
            sendMessage(chatId, Messages.HELP.messageText);
        } else if(command == Commands.MENU) {
            sendMessage(chatId, "Тут будет контекстное меню, а также могла бы быть ваша реклама :)");
        }

//        if (!userService.checkByChatId(chatId)) {
//            if (findCommandByString(incomingMessage) == Commands.START) {
//                message = new SendMessage(chatId, Constants.REQUEST_START);
//                userService.addUser(new User(update.message().chat().firstName(), update.message().chat().lastName(), update.message().chat().username(), null, null, update.message().chat().id()));
//                chatConfigService.addChatConfig(new ChatConfig(chatId, 0L));
//                SendMessage sendMessage = new SendMessage(chatId, Constants.START_DESCRIPTION);
//                SendResponse sendResponse = telegramBot.execute(sendMessage); //это кинуть в отдельный метод, т.к. в этом не вызывается или порешать с ошибкой
//
//                SendMessage keys = new SendMessage(update.message().chat().id(), Constants.CHOOSE_OPTION);
//                keys.replyMarkup(KeyboardFactory.startButtons());
//                SendResponse response1 = telegramBot.execute(keys);
//            } else if (!incomingMessage.equalsIgnoreCase("/start")) {
//                SendMessage sendMessage = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
//                SendResponse sendResponse = telegramBot.execute(sendMessage);
//            }
//        } else statusDeterminant(chatId, update);
    }


    private UpdateType checkingUpdate(Update update) {
        if (update.message() != null) {
            if(update.message().text() != null) {
                if (update.message().text().startsWith("/")) {
                    return UpdateType.COMMAND;
                } else {
                    return UpdateType.MESSAGE;
                }
            } else if (update.message().photo() != null) {
                return UpdateType.PHOTO;
            } else {
                return UpdateType.ERROR;
            }

//            PhotoSize[] incomingMessage = update.message().photo();
//            Long chatId = update.message().chat().id();
//            if (!userService.checkByChatId(chatId)) {
//                SendMessage message = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
//                SendResponse response = telegramBot.execute(message);
//            } else if (chatConfigService.findByChatId(chatId).equals(Constants.KEEPING_a_PET)) {
//                // проверка наличия текста DailyReport, если есть, то ок, если нет - запрос
//            } else {
//                SendMessage message = new SendMessage(chatId, "У Вас нет животного на адаптации, вы не можете направить фотоотчёт." + Constants.CHOOSE_OPTION + Commands.MENU);
//                SendResponse response = telegramBot.execute(message);
//            }
        } else if (update.callbackQuery() != null) { // Callback answer processing
            return UpdateType.CALL_BACK_QUERY;
//            Long chatId = update.message().chat().id();
//            SendMessage message = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
//            SendResponse response = telegramBot.execute(message);

        } else {
            return UpdateType.ERROR;
        }
    }

    private SendResponse statusDeterminant(Long chatId, Update update) {
        Long botStatus = chatConfigService.findByChatId(chatId).getChatId();
        SendMessage message = new SendMessage(chatId, Constants.CHOOSE_OPTION);
        SendResponse response = telegramBot.execute(message);
        BotStatus bStatus = getBotStatusByLong(botStatus);
        switch (bStatus) {
            case DEFAULT:
                startButtons(update);
                break;
            case CONSULT_NEW_USER:
                consultNewUser(update);
                break;
            case CONSULT_POTENTIAL_OWNER:
                consultPotentialOwner(update);
                break;
            case KEEPING_a_PET:
                keepingPet(update);
                break;
            // Не реализовано
            case CHAT_WITH_VOLUNTEER://либо создать метод
                message = new SendMessage(chatId, Constants.CALL_VOLUNTEER);
                response = telegramBot.execute(message);
                break;
            default:
                Exception exception; // не знаю нужно ли цеплять
        }
        return response;
    }

    private BotStatus getBotStatusByLong(Long botStatus) {
        for (BotStatus s : BotStatus.values()) {
            if(s.equals(botStatus))
                return s;
        }
        return BotStatus.DEFAULT;
    }

    private void keepingPet(Update update) {
        String message = Constants.KEEPING_a_PET;
        sendMessageWithKeyboard(update, message, KeyboardFactory.stageThree());
    }

    private void consultPotentialOwner(Update update) {
        String message = Constants.CONSULT_POTENTIAL_OWNER;
        sendMessageWithKeyboard(update, message, KeyboardFactory.stageTwo());
    }

    private void consultNewUser(Update update) {
        String message = " Здесь некоторая информация о нашем приюте.";
        sendMessageWithKeyboard(update, message, KeyboardFactory.stageOne());

    }

    private void startButtons(Update update) {
        String message = "Привет, " + update.message().chat().firstName() + "!" + Constants.CHOOSE_OPTION;
        sendMessageWithKeyboard(update, message, KeyboardFactory.startButtons());
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(sendMessage);
        if (response.isOk()) {
            logger.info("message: {} is sent ", message);
        } else {
            logger.warn("Message was not sent. Error code:  " + response.errorCode());
        }
    }

    private void sendMessageWithKeyboard(Update update, String message, InlineKeyboardMarkup keyboardMarkup) {
        Long chatId = update.message().chat().id();
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message).replyMarkup(keyboardMarkup));
        if (response.isOk()) {
            logger.info("message: {} is sent ", message);
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
