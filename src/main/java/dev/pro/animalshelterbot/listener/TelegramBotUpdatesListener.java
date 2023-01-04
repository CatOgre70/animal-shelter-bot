package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.exception.ChatConfigNotFoundException;
import dev.pro.animalshelterbot.factory.KeyboardFactory;
import dev.pro.animalshelterbot.constants.*;
import dev.pro.animalshelterbot.menu.ButtonType;
import dev.pro.animalshelterbot.menu.Buttons;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.DailyReportService;
import dev.pro.animalshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * The main service of the bot containing the logic of processing incoming updates
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ChatConfigService chatConfigService;

    private final DailyReportService dailyReportService;

    private final UserService userService;


    public TelegramBotUpdatesListener(TelegramBot telegramBot, ChatConfigService chatConfigService,
                                      UserService userService, DailyReportService dailyReportService) {
        this.telegramBot = telegramBot;
        this.chatConfigService = chatConfigService;
        this.userService = userService;
        this.dailyReportService = dailyReportService;
    }


    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * defining bot actions depending on user status
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            // Process your updates here
            logger.info("Processing update: {}", update);
            Long chatId = 0L;
            String firstName = null, lastName = null, nickName = null;
            ChatState chatState;

            UpdateType updateType = checkingUpdate(update);
            if(updateType == UpdateType.COMMAND || updateType == UpdateType.MESSAGE || updateType == UpdateType.PHOTO) {
                chatId = update.message().chat().id();
                firstName = update.message().from().firstName();
                lastName = update.message().from().lastName();
                nickName = update.message().from().username();
            } else if(updateType == UpdateType.CALL_BACK_QUERY) {
                chatId = update.callbackQuery().from().id();
                firstName = update.callbackQuery().from().firstName();
                lastName = update.callbackQuery().from().lastName();
                nickName = update.callbackQuery().from().username();
            }

            Optional<ChatConfig> chatConfigResult = chatConfigService.findByChatId(chatId);
            Optional<User> userResult = userService.findByChatId(chatId);
            User user;
            ChatConfig chatConfig;

            if(chatConfigResult.isEmpty() && userResult.isEmpty()) { // New user
                user = new User(firstName, lastName, nickName, null, null, chatId);
                userService.addUser(user);
                chatConfig = new ChatConfig(chatId, ChatState.AWAITING_SHELTER, null);
                chatConfigService.addChatConfig(chatConfig);
                chatState = ChatState.NEW_USER;
            } else { // Not new user
                user = userResult.get();
                chatConfig = chatConfigResult.get();
                chatState = chatConfig.getChatState();
            }

            switch(chatState) {
                case NEW_USER:
                    sendMessage(chatId, Messages.WELCOME_TO_THE_CHATBOT.messageText);
                    sendMessage(chatId, Messages.CHOOSE_SHELTER.messageText);
                    sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
                    chatConfig.setChatState(ChatState.AWAITING_SHELTER);
                    chatConfigService.editChatConfig(chatConfig);
                    break;
                case AWAITING_SHELTER:
                    if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DOG_SHELTER.bCallBack)) {
                        user.setShelter(Shelter.DOG_SHELTER);
                        userService.editUser(user);
                        chatConfig.setShelter(Shelter.DOG_SHELTER);
                        chatConfig.setChatState(ChatState.DEFAULT);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.SHELTER_CHOSEN.messageText +
                                chatConfig.getShelter().shelterSpecialization + Messages.SHELTER_CHOSEN1);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.CAT_SHELTER.bCallBack)) {
                        user.setShelter(Shelter.CAT_SHELTER);
                        userService.editUser(user);
                        chatConfig.setShelter(Shelter.CAT_SHELTER);
                        chatConfig.setChatState(ChatState.DEFAULT);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.SHELTER_CHOSEN.messageText +
                                chatConfig.getShelter().shelterSpecialization + Messages.SHELTER_CHOSEN1.messageText);
                    } else {
                        sendMessage(chatId, Messages.CHOOSE_SHELTER1.messageText);
                        sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
                    }
                    break;
                case SHELTER_CHOSEN:
                case DEFAULT:
                case CONSULT_NEW_USER:
                case CONSULT_POTENTIAL_OWNER:
                case KEEPING_a_PET:
                case CHAT_WITH_VOLUNTEER:
                case AWAITING_GENERAL_WELL_BEING:
                case AWAITING_DIET:
                case AWAITING_CHANGE_IN_BEHAVIOR:
                case AWAITING_PHOTO:
                case AWAITING_ADDRESS:
                case AWAITING_PHONE:
            }

//            switch (updateType) {
//                case COMMAND:
//                    processCommands(update, chatState);
//                    break;
//                case MESSAGE:
//                    processMessages(update, chatState);
//                    break;
//                case PHOTO:
//                    processPhotos(update, botStatus);
//                    break;
//                case CALL_BACK_QUERY:
//                    processCallbackQueries(update, botStatus);
//                    break;
//                case ERROR:
//                    processErrors(update, botStatus);
//                    break;
//            }

//            statusDeterminant(update.message().chat().id(), update);

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * commands that the user sends
     */
    private void processCommands(Update update, ChatState chatState) {
        Long chatId = update.message().chat().id();
        Commands command = Commands.valueOfCommandText(update.message().text());
        if(command == null) {
            sendMessage(chatId, Messages.THERE_IS_NO_SUCH_COMMAND.messageText);
            sendMessage(chatId, Messages.HELP.messageText);
            return;
        }
        if(command == Commands.START) {
            switch (chatState) {
                case NEW_USER:
                    sendMessage(chatId, Messages.WELCOME_TO_THE_CHATBOT.messageText);
                    sendMessage(chatId, Messages.CHOOSE_SHELTER.messageText);
                    sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
                    break;
                case AWAITING_SHELTER:
                    sendMessage(chatId, Messages.CHOOSE_SHELTER1.messageText);
                    sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
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


    private void processMessages(Update update, ChatState chatState) {
        Long chatId = update.message().chat().id();
        DailyReport currentDailyReport;
        if(chatState == ChatState.AWAITING_GENERAL_WELL_BEING) {
            currentDailyReport = dailyReportService.findDailyReportByChatId(chatId);
            if(currentDailyReport == null) { // new daily report
                LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                currentDailyReport = new DailyReport(localDateTime, null, null, null,
                        null, null, update.message().text(), null);
                dailyReportService.addDailyReport(currentDailyReport);
                sendMessage(chatId, Messages.GENERAL_WELL_BEING_RECEIVED.messageText);
                Optional<ChatConfig> chatConfigResult = chatConfigService.findByChatId(chatId);
                if(chatConfigResult.isEmpty()) {
                    throw new ChatConfigNotFoundException("Запись конфигурации чата с таким chatId не найдена!");
                } else {
                    chatConfigResult.get().setChatState(ChatState.KEEPING_a_PET);
                    chatConfigService.editChatConfig(chatConfigResult.get());
                }
            } else {

            }
        }


    }



    /**
     * user stage check
     */
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

    /**
     * user menu
     * param chatId must not be null
     * param update must not be null
     * return user response
     */
    private SendResponse statusDeterminant(Long chatId, Update update) {
        Long botStatus = chatConfigService.findByChatId(chatId).get().getChatId();
        SendMessage message = new SendMessage(chatId, Constants.CHOOSE_OPTION);
        SendResponse response = telegramBot.execute(message);
        ChatState bStatus = getBotStatusByLong(botStatus);
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

    private ChatState getBotStatusByLong(Long botStatus) {
        for (ChatState s : ChatState.values()) {
            if(s.equals(botStatus))
                return s;
        }
        return ChatState.DEFAULT;
    }

    private void keepingPet(Update update) {
        KeyboardFactory.stageThree();
    }

    private void consultPotentialOwner(Update update) {
        KeyboardFactory.stageTwo();
    }

    private void consultNewUser(Update update) {
        KeyboardFactory.stageOne();
    }

    private void startButtons(Update update) {
        KeyboardFactory.startButtons();
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

    private void sendMenu(Long chatId, String menuHeader, Buttons... buttons) {
        InlineKeyboardButton[] inlineKeyboardButtons = new InlineKeyboardButton[buttons.length];
        for(int i = 0; i < buttons.length; i++) {
            if(buttons[i].bType.equals(ButtonType.CALLBACK)) {
                inlineKeyboardButtons[i] = new InlineKeyboardButton(buttons[i].bText).callbackData(buttons[i].bCallBack);
            } else {
                inlineKeyboardButtons[i] = new InlineKeyboardButton(buttons[i].bText).url(buttons[i].url);
            }
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(inlineKeyboardButtons);
        SendMessage message = new SendMessage(chatId, menuHeader);
        message.replyMarkup(inlineKeyboard);
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            logger.error("Response error: {} {}", response.errorCode(), response.message());
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
