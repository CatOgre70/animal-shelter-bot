package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.constants.BotStatus;
import dev.pro.animalshelterbot.constants.Commands;
import dev.pro.animalshelterbot.constants.Constants;
import dev.pro.animalshelterbot.factory.KeyboardFactory;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.model.User;
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


            checkingUpdate(update);
            checkStartMessage(update);
            scanUpdates(update);
            statusDeterminant(update);
            sendReplies(update);


        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void scanUpdates(Update update) {
    }


    private void sendReplies(Update update) {
        String message = "";
        Commands commands = null;  // Опасно, т.к. может вызвать NPE
        switch (commands) {
            case CONSULT_NEW_USER:
                consultNewUser(update);
                break;
            case CONSULT_POTENTIAL_OWNER:
                consultPotentialOwner(update);
                break;
            case DAILY_REPORT:
                keepingPet(update);
                break;
            case ABOUT:
                message = Constants.ABOUT;
                break;
            case SCHEDULE:
                message = Constants.SCHEDULE;
                break;
            case ADDRESS:
                message = Constants.ADDRESS;
                break;
            case PRECAUTIONS:
                message = Constants.PRECAUTIONS;
                break;
            case DATING_RULES:
                message = Constants.DATING_RULES;
                break;
            case DOCS_LIST:
                message = Constants.DOCS_LIST;
                break;
            case TRANSPORTATION:
                message = Constants.TRANSPORTATION;
                break;
            case PUPPY_IMPROVEMENT:
                message = Constants.PUPPY_IMPROVEMENT;
                break;
            case ADULT_IMPROVEMENT:
                message = Constants.ADULT_IMPROVEMENT;
                break;
            case DISABILITIES_IMPROVEMENTS:
                message = Constants.DISABILITIES_IMPROVEMENTS;
                break;
            case DOG_HANDLERS_TIPS:
                message = Constants.DOG_HANDLERS_TIPS;
                break;
            case PROVEN_DOG_HANDLERS:
                message = Constants.PROVEN_DOG_HANDLERS;
                break;
            case REASONS_FOR_REFUSAL:
                message = Constants.REASONS_FOR_REFUSAL;
        }
        sendMessage(update, message);
    }

    private void statusDeterminant(Update update) {
        Long chatId = update.message().chat().id();
        Long botStatus = chatConfigService.findByChatId(chatId);
        String message = Constants.CHOOSE_OPTION;
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
            case CHAT_WITH_VOLUNTEER:
                message = Constants.CALL_VOLUNTEER;
                break;
            default:
                Exception exception;
        }

    }

    private void checkStartMessage(Update update) {
        Long chatId = update.message().chat().id();
        String incomingMessage = update.message().text();
        if (!userService.checkByChatId(chatId)) {
            if (incomingMessage.equalsIgnoreCase(Commands.START.getS())) {
                userService.addUser(new User(update.message().chat().firstName(), update.message().chat().lastName(), update.message().chat().username(), null, null, update.message().chat().id()));
                //Здесь беда в том, что ChatConfig создаётся в базе со значением по умолчанию (0L), вместо приходящего chatId
                chatConfigService.addChatConfig(new ChatConfig(chatId, 0L));
                //Или тупо стрингом прописать? Взлетит ли String без chatId?
                SendMessage sendMessage = new SendMessage(chatId, Constants.START_DESCRIPTION + Constants.CHOOSE_OPTION);
                sendMessageWithKeyboard(update, sendMessage.toString(), KeyboardFactory.startButtons());
            }   SendMessage sendMessage = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
                SendResponse sendResponse = telegramBot.execute(sendMessage);

        } else statusDeterminant(update);
    }

    private String checkingUpdate(Update update) {
        if (update.message() != null) {
            String incomingMessage = update.message().text();
            Long chatId = update.message().chat().id();
        } else if (update.message().photo() != null) {
            PhotoSize[] incomingMessage = update.message().photo();
            Long chatId = update.message().chat().id();
            if (!userService.checkByChatId(chatId)) {
                SendMessage message = new SendMessage(chatId, "Вы ошиблись с вводом команды. " + Constants.REQUEST_START);
                SendResponse response = telegramBot.execute(message);
            } else if (chatConfigService.findByChatId(chatId).equals(Constants.KEEPING_a_PET)) {
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
        return Constants.REQUEST_START;
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

    private void sendMessage(Update update, String message) {
        Long chatId = update.message().chat().id();
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
