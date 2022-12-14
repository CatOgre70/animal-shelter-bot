package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import dev.pro.animalshelterbot.exception.AnimalNotFoundException;
import dev.pro.animalshelterbot.exception.ChatConfigNotFoundException;
import dev.pro.animalshelterbot.factory.KeyboardFactory;
import dev.pro.animalshelterbot.constants.*;
import dev.pro.animalshelterbot.menu.ButtonType;
import dev.pro.animalshelterbot.menu.Buttons;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.AnimalService;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.DailyReportService;
import dev.pro.animalshelterbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static dev.pro.animalshelterbot.constants.BotStatus.KEEPING_a_PET;

/**
 * The main service of the bot containing the logic of processing incoming updates
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    /**
     * event recording process
     */
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ChatConfigService chatConfigService;

    private final DailyReportService dailyReportService;

    private final UserService userService;

    private final AnimalService animalService;


    public TelegramBotUpdatesListener(TelegramBot telegramBot, ChatConfigService chatConfigService,
                                      UserService userService, DailyReportService dailyReportService,
                                      AnimalService animalService) {
        this.telegramBot = telegramBot;
        this.chatConfigService = chatConfigService;
        this.userService = userService;
        this.dailyReportService = dailyReportService;
        this.animalService = animalService;
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
            Shelter shelter;

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
                        shelter = Shelter.DOG_SHELTER;
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.CAT_SHELTER.bCallBack)) {
                        shelter = Shelter.CAT_SHELTER;
                    } else {
                        sendMessage(chatId, Messages.CHOOSE_SHELTER1.messageText);
                        sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
                        break;
                    }
                    user.setShelter(shelter);
                    userService.editUser(user);
                    chatConfig.setShelter(shelter);
                    chatConfig.setChatState(ChatState.DEFAULT);
                    chatConfigService.editChatConfig(chatConfig);
                    sendMessage(chatId, Messages.SHELTER_CHOSEN.messageText +
                            chatConfig.getShelter().shelterSpecialization.toLowerCase() +
                            Messages.SHELTER_CHOSEN1.messageText);
                    sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    sendMenu(chatId, "Главное меню. Что вы хотите сделать дальше:", Buttons.BACK,
                            Buttons.SHELTER_INFO, Buttons.ANIMAL_INFO, Buttons.DAILY_REPORT, Buttons.HELP,
                            Buttons.CALL_VOLUNTEER);
                    break;
                case SHELTER_CHOSEN:
                    break;
                case DEFAULT:
                    if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.SHELTER_INFO.bCallBack)) {
                        // Processing Information about shelter button
                        shelter = chatConfig.getShelter();
                        chatConfig.setChatState(ChatState.CONSULT_NEW_USER);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        if (shelter == Shelter.DOG_SHELTER) {
                            sendMenu(chatId, "Информация о приюте для собак", Buttons.BACK,
                                    Buttons.DOG_SHELTER_INFO, Buttons.DOG_SHELTER_ADDRESS, Buttons.DOG_SHELTER_SECURITY,
                                    Buttons.DOG_SHELTER_SAFETY_TIPS, Buttons.SEND_PHONE_AND_ADDRESS,
                                    Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        } else {
                            sendMenu(chatId, "Информация о приюте для кошек", Buttons.BACK,
                                    Buttons.CAT_SHELTER_INFO, Buttons.CAT_SHELTER_ADDRESS, Buttons.CAT_SHELTER_SECURITY,
                                    Buttons.CAT_SHELTER_SAFETY_TIPS, Buttons.SEND_PHONE_AND_ADDRESS,
                                    Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        }
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.ANIMAL_INFO.bCallBack)) {
                        // Processing How to adopt animal button
                        shelter = chatConfig.getShelter();
                        chatConfig.setChatState(ChatState.CONSULT_POTENTIAL_OWNER);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        if (shelter == Shelter.DOG_SHELTER) {
                            sendMenu(chatId, "Информация о том, как взять собаку из приюта", Buttons.BACK,
                                    Buttons.DOG_ANIMAL_WELCOME, Buttons.DOG_ANIMAL_DOCS, Buttons.DOG_ANIMAL_TRANSPORT,
                                    Buttons.DOG_ANIMAL_HOME1, Buttons.DOG_ANIMAL_HOME2, Buttons.DOG_ANIMAL_HOME3,
                                    Buttons.DOG_ANIMAL_FIRST, Buttons.DOG_ANIMAL_PROF, Buttons.DOG_ANIMAL_REJECT,
                                    Buttons.SEND_PHONE_AND_ADDRESS, Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        } else {
                            sendMenu(chatId, "Информация о том, как взять кошку из приюта", Buttons.BACK,
                                    Buttons.CAT_ANIMAL_WELCOME, Buttons.CAT_ANIMAL_DOCS, Buttons.CAT_ANIMAL_TRANSPORT,
                                    Buttons.CAT_ANIMAL_HOME1, Buttons.CAT_ANIMAL_HOME2, Buttons.CAT_ANIMAL_HOME3,
                                    Buttons.CAT_ANIMAL_REJECT,
                                    Buttons.SEND_PHONE_AND_ADDRESS, Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        }
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DAILY_REPORT.bCallBack)) {
                        // Processing Send daily report button

                        chatConfig.setChatState(ChatState.KEEPING_a_PET);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.DAILY_REPORT_MENU_WELCOME.messageText);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        sendMenu(chatId, "Части отчета", Buttons.BACK, Buttons.DR_GENERAL_WELL_BEING,
                                Buttons.DR_DIET, Buttons.DR_CHANGE_IN_BEHAVIOR, Buttons.DR_PHOTO,
                                Buttons.HELP, Buttons.CALL_VOLUNTEER);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.BACK.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.UP.commandText))) {
                        // Processing "Back to previous Menu" button or /up command
                        chatConfig.setShelter(null);
                        chatConfig.setChatState(ChatState.AWAITING_SHELTER);
                        chatConfigService.editChatConfig(chatConfig);
                        user.setShelter(null);
                        userService.editUser(user);
                        sendMessage(chatId, Messages.CHOOSE_SHELTER1.messageText);
                        sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.HELP.bCallBack)) || (updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.HELP.commandText))) {
                        // Processing Help button or /help command
                        sendMessage(chatId, Messages.HELP.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // We are not processing messages at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.START.commandText)){
                        // This is wrong command at this stage
                        sendMessage(chatId, Messages.WRONG_COMMAND.messageText);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.MENU.commandText)) {
                        // Processing /menu command
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        sendMenu(chatId, "Главное меню. Что вы хотите сделать дальше:", Buttons.BACK,
                                Buttons.SHELTER_INFO, Buttons.ANIMAL_INFO, Buttons.DAILY_REPORT, Buttons.HELP,
                                Buttons.CALL_VOLUNTEER);
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case CONSULT_NEW_USER:
                    if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DOG_SHELTER_INFO.bCallBack)) {
                        // Processing Information about Dog shelter common info button
                        sendMessage(chatId, Messages.DOG_SHELTER_GENERAL_INFO.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.CAT_SHELTER_INFO.bCallBack)) {
                        // Processing Information about Cat shelter common info button
                        sendMessage(chatId, Messages.CAT_SHELTER_GENERAL_INFO.messageText);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.BACK.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                                    update.message().text().equalsIgnoreCase(Commands.UP.commandText))) {
                        // Processing "Back to previous Menu" button or /up command
                        chatConfig.setChatState(ChatState.DEFAULT);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        sendMenu(chatId, "Главное меню. Что вы хотите сделать дальше:", Buttons.BACK,
                                Buttons.SHELTER_INFO, Buttons.ANIMAL_INFO, Buttons.DAILY_REPORT, Buttons.HELP,
                                Buttons.CALL_VOLUNTEER);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.HELP.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.HELP.commandText))) {
                        // Processing Help button or /help command
                        sendMessage(chatId, Messages.HELP.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // We are not processing messages at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.START.commandText)){
                        // This is wrong command at this stage
                        sendMessage(chatId, Messages.WRONG_COMMAND.messageText);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.MENU.commandText)) {
                        // Processing /menu command
                        shelter = chatConfig.getShelter();
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        if (shelter == Shelter.DOG_SHELTER) {
                            sendMenu(chatId, "Информация о приюте для собак", Buttons.BACK,
                                    Buttons.DOG_SHELTER_INFO, Buttons.DOG_SHELTER_ADDRESS, Buttons.DOG_SHELTER_SECURITY,
                                    Buttons.DOG_SHELTER_SAFETY_TIPS, Buttons.SEND_PHONE_AND_ADDRESS,
                                    Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        } else {
                            sendMenu(chatId, "Информация о приюте для кошек", Buttons.BACK,
                                    Buttons.CAT_SHELTER_INFO, Buttons.CAT_SHELTER_ADDRESS, Buttons.CAT_SHELTER_SECURITY,
                                    Buttons.CAT_SHELTER_SAFETY_TIPS, Buttons.SEND_PHONE_AND_ADDRESS,
                                    Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        }
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case CONSULT_POTENTIAL_OWNER:
                    if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DOG_ANIMAL_WELCOME.bCallBack)) {
                        // Processing How to meet animals at a shelter button
                        sendMessage(chatId, Messages.DOG_ANIMAL_WELCOME.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.CAT_ANIMAL_WELCOME.bCallBack)) {
                        // Processing How to meet animals at a shelter button
                        sendMessage(chatId, Messages.CAT_ANIMAL_WELCOME.messageText);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.BACK.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                                    update.message().text().equalsIgnoreCase(Commands.UP.commandText))) {
                        // Processing "Back to previous Menu" button or /up command
                        chatConfig.setChatState(ChatState.DEFAULT);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        sendMenu(chatId, "Главное меню. Что вы хотите сделать дальше:", Buttons.BACK,
                                Buttons.SHELTER_INFO, Buttons.ANIMAL_INFO, Buttons.DAILY_REPORT, Buttons.HELP,
                                Buttons.CALL_VOLUNTEER);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.HELP.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                                    update.message().text().equalsIgnoreCase(Commands.HELP.commandText))) {
                        // Processing Help button or /help command
                        sendMessage(chatId, Messages.HELP.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // We are not processing messages at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.START.commandText)){
                        // This is wrong command at this stage
                        sendMessage(chatId, Messages.WRONG_COMMAND.messageText);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.MENU.commandText)) {
                        // Processing /menu command
                        shelter = chatConfig.getShelter();
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        if (shelter == Shelter.DOG_SHELTER) {
                            sendMenu(chatId, "Информация о том, как взять собаку из приюта", Buttons.BACK,
                                    Buttons.DOG_ANIMAL_WELCOME, Buttons.DOG_ANIMAL_DOCS, Buttons.DOG_ANIMAL_TRANSPORT,
                                    Buttons.DOG_ANIMAL_HOME1, Buttons.DOG_ANIMAL_HOME2, Buttons.DOG_ANIMAL_HOME3,
                                    Buttons.DOG_ANIMAL_FIRST, Buttons.DOG_ANIMAL_PROF, Buttons.DOG_ANIMAL_REJECT,
                                    Buttons.SEND_PHONE_AND_ADDRESS, Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        } else {
                            sendMenu(chatId, "Информация о том, как взять кошку из приюта", Buttons.BACK,
                                    Buttons.CAT_ANIMAL_WELCOME, Buttons.CAT_ANIMAL_DOCS, Buttons.CAT_ANIMAL_TRANSPORT,
                                    Buttons.CAT_ANIMAL_HOME1, Buttons.CAT_ANIMAL_HOME2, Buttons.CAT_ANIMAL_HOME3,
                                    Buttons.CAT_ANIMAL_REJECT,
                                    Buttons.SEND_PHONE_AND_ADDRESS, Buttons.HELP, Buttons.CALL_VOLUNTEER);
                        }
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case KEEPING_a_PET:
                    if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DR_GENERAL_WELL_BEING.bCallBack)) {
                        // Processing Send general well-being button
                        chatConfig.setChatState(ChatState.AWAITING_GENERAL_WELL_BEING);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.SEND_GENERAL_WELL_BEING.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DR_DIET.bCallBack)) {
                        // Processing Send diet button
                        chatConfig.setChatState(ChatState.AWAITING_DIET);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.SEND_DIET.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DR_CHANGE_IN_BEHAVIOR.bCallBack)) {
                        // Processing Send change in behaviour button
                        chatConfig.setChatState(ChatState.AWAITING_CHANGE_IN_BEHAVIOR);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.DR_PHOTO.bCallBack)) {
                        // Processing Send photo button
                        chatConfig.setChatState(ChatState.AWAITING_PHOTO);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.SEND_PHOTO.messageText);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.BACK.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                                    update.message().text().equalsIgnoreCase(Commands.UP.commandText))) {
                        // Processing "Back to previous menu" button or /up command
                        chatConfig.setChatState(ChatState.DEFAULT);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        sendMenu(chatId, "Главное меню. Что вы хотите сделать дальше:", Buttons.BACK,
                                Buttons.SHELTER_INFO, Buttons.ANIMAL_INFO, Buttons.DAILY_REPORT, Buttons.HELP,
                                Buttons.CALL_VOLUNTEER);
                    } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                            update.callbackQuery().data().equals(Buttons.HELP.bCallBack)) ||
                            (updateType == UpdateType.COMMAND &&
                                    update.message().text().equalsIgnoreCase(Commands.HELP.commandText))) {
                        // Processing Help button or /help command
                        sendMessage(chatId, Messages.HELP.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // We are not processing messages at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.START.commandText)){
                        // This is wrong command at this stage
                        sendMessage(chatId, Messages.WRONG_COMMAND.messageText);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                    } else if(updateType == UpdateType.COMMAND &&
                            update.message().text().equalsIgnoreCase(Commands.MENU.commandText)) {
                        // Processing /menu command
                        sendMessage(chatId, Messages.DAILY_REPORT_MENU_WELCOME.messageText);
                        sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
                        sendMenu(chatId, "Части отчета", Buttons.BACK, Buttons.DR_GENERAL_WELL_BEING,
                                Buttons.DR_DIET, Buttons.DR_CHANGE_IN_BEHAVIOR, Buttons.DR_PHOTO,
                                Buttons.HELP, Buttons.CALL_VOLUNTEER);
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case CHAT_WITH_VOLUNTEER:
                case AWAITING_GENERAL_WELL_BEING:
                    if(updateType == UpdateType.COMMAND) {
                        // We are not processing commands on this stage
                        sendMessage(chatId, Messages.SEND_GENERAL_WELL_BEING.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // Processing message as a general well-being update
                        DailyReport currentDailyReport;
                        Optional<DailyReport> result = dailyReportService.findDailyReportByChatId(chatId);
                        if(result.isEmpty()) { // new daily report
                            LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                            currentDailyReport = new DailyReport(localDateTime, null, null, null,
                                    null, null, update.message().text(), null, null);
                            Optional<Animal> animalResult = animalService.getAnimalByChatId(chatId);
                            if(animalResult.isEmpty()) {
                                logger.error("Adopted animal of user with such chatId was not found in the database");
                                throw new AnimalNotFoundException("Adopted animal of user with such chatId " +
                                        "was not found in the database");
                            } else {
                                currentDailyReport.setAnimalId(animalResult.get().getId());
                            }
                            dailyReportService.addDailyReport(currentDailyReport);
                        } else { // Existing daily report
                            currentDailyReport = result.get();
                            currentDailyReport.setGeneralWellBeing(update.message().text());
                            dailyReportService.editDailyReport(currentDailyReport);
                        }
                        chatConfig.setChatState(ChatState.KEEPING_a_PET);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.GENERAL_WELL_BEING_RECEIVED.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.SEND_GENERAL_WELL_BEING.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY) {
                        // We are not processing callback queries at this stage
                        sendMessage(chatId, Messages.SEND_GENERAL_WELL_BEING.messageText);
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case AWAITING_DIET:
                    if(updateType == UpdateType.COMMAND) {
                        // We are not processing commands on this stage
                        sendMessage(chatId, Messages.SEND_DIET.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // Processing message as a diet update
                        DailyReport currentDailyReport;
                        Optional<DailyReport> result = dailyReportService.findDailyReportByChatId(chatId);
                        if(result.isEmpty()) { // new daily report
                            LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                            currentDailyReport = new DailyReport(localDateTime, null, null, null,
                                    null, update.message().text(), null, null, null);
                            Optional<Animal> animalResult = animalService.getAnimalByChatId(chatId);
                            if(animalResult.isEmpty()) {
                                logger.error("Adopted animal of user with such chatId was not found in the database");
                                throw new AnimalNotFoundException("Adopted animal of user with such chatId " +
                                        "was not found in the database");
                            } else {
                                currentDailyReport.setAnimalId(animalResult.get().getId());
                            }
                            dailyReportService.addDailyReport(currentDailyReport);
                        } else { // Existing daily report
                            currentDailyReport = result.get();
                            currentDailyReport.setDiet(update.message().text());
                            dailyReportService.editDailyReport(currentDailyReport);
                        }
                        chatConfig.setChatState(ChatState.KEEPING_a_PET);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.DIET_RECEIVED.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY) {
                        // We are not processing callback queries at this stage
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case AWAITING_CHANGE_IN_BEHAVIOR:
                    if(updateType == UpdateType.COMMAND) {
                        // We are not processing commands on this stage
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // Processing message as a change in behavior update
                        DailyReport currentDailyReport;
                        Optional<DailyReport> result = dailyReportService.findDailyReportByChatId(chatId);
                        if(result.isEmpty()) { // new daily report
                            LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                            currentDailyReport = new DailyReport(localDateTime, null, null, null,
                                    null, null, null, update.message().text(), null);
                            Optional<Animal> animalResult = animalService.getAnimalByChatId(chatId);
                            if(animalResult.isEmpty()) {
                                logger.error("Adopted animal of user with such chatId was not found in the database");
                                throw new AnimalNotFoundException("Adopted animal of user with such chatId " +
                                        "was not found in the database");
                            } else {
                                currentDailyReport.setAnimalId(animalResult.get().getId());
                            }
                            dailyReportService.addDailyReport(currentDailyReport);
                        } else { // Existing daily report
                            currentDailyReport = result.get();
                            currentDailyReport.setChangeInBehavior(update.message().text());
                            dailyReportService.editDailyReport(currentDailyReport);
                        }
                        chatConfig.setChatState(ChatState.KEEPING_a_PET);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.CHANGE_IN_BEHAVIOR_RECEIVED.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // We are not processing photos at this stage
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY) {
                        // We are not processing callback queries at this stage
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;
                case AWAITING_PHOTO:
                    if(updateType == UpdateType.COMMAND) {
                        // We are not processing commands on this stage
                        sendMessage(chatId, Messages.SEND_PHOTO.messageText);
                    } else if(updateType == UpdateType.MESSAGE) {
                        // We are not processing messages at this stage
                        sendMessage(chatId, Messages.SEND_PHOTO.messageText);
                    } else if(updateType == UpdateType.PHOTO) {
                        // Processing photo
                        DailyReport currentDailyReport;
                        Optional<DailyReport> result = dailyReportService.findDailyReportByChatId(chatId);
                        if(result.isEmpty()) { // new daily report
                            LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                            currentDailyReport = new DailyReport(localDateTime, null, null, null,
                                    null, null, null, null, null);
                            Optional<Animal> animalResult = animalService.getAnimalByChatId(chatId);
                            if(animalResult.isEmpty()) {
                                logger.error("Adopted animal of user with such chatId was not found in the database");
                                throw new AnimalNotFoundException("Adopted animal of user with such chatId " +
                                        "was not found in the database");
                            } else {
                                currentDailyReport.setAnimalId(animalResult.get().getId());
                            }
                            currentDailyReport = dailyReportService.addDailyReport(currentDailyReport);
                        } else { // Existing daily report
                            currentDailyReport = result.get();
                        }
                        // Sent photo processing
                        int photoSizeArrayLength = update.message().photo().length;
                        String fileId = update.message().photo()[photoSizeArrayLength-1].fileId();
                        GetFile request = new GetFile(fileId);
                        GetFileResponse getFileResponse = telegramBot.execute(request);
                        File file = getFileResponse.file();
                        String fullPath = telegramBot.getFullFilePath(file);
                        try {
                            dailyReportService.downloadAndUploadPhoto(currentDailyReport.getId(), fullPath,
                                    file.fileSize(), "image/jpeg");
                        } catch(IOException e) {
                            logger.error(Arrays.toString(e.getStackTrace()));
                        }
                        chatConfig.setChatState(ChatState.KEEPING_a_PET);
                        chatConfigService.editChatConfig(chatConfig);
                        sendMessage(chatId, Messages.PHOTO_RECEIVED.messageText);
                    } else if(updateType == UpdateType.CALL_BACK_QUERY) {
                        // We are not processing callback queries at this stage
                        sendMessage(chatId, Messages.SEND_CHANGE_IN_BEHAVIOR.messageText);
                    } else {
                        logger.error("Unrecognized update!");
                    }
                    break;

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

    /**
     * НА ДАННЫЙ МОМЕНТ МЕТОД НЕ ИСПОЛЬЗУЕТСЯ
     * @param update
     * @param chatState
     */
    private void processMessages(Update update, ChatState chatState) {
        Long chatId = update.message().chat().id();
        DailyReport currentDailyReport;
        if(chatState == ChatState.AWAITING_GENERAL_WELL_BEING) {
            Optional<DailyReport> result = dailyReportService.findDailyReportByChatId(chatId);
            if(result.isEmpty()) { // new daily report
                LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                currentDailyReport = new DailyReport(localDateTime, null, null, null,
                        null, null, update.message().text(), null, null);
                dailyReportService.addDailyReport(currentDailyReport);
                sendMessage(chatId, Messages.GENERAL_WELL_BEING_RECEIVED.messageText);
                Optional<ChatConfig> chatConfigResult = chatConfigService.findByChatId(chatId);
                if(chatConfigResult.isEmpty()) {
                    throw new ChatConfigNotFoundException("Запись конфигурации чата с таким chatId не найдена!");
                } else {
                    chatConfigResult.get().setChatState(ChatState.KEEPING_a_PET);
                    chatConfigService.editChatConfig(chatConfigResult.get());
                }
            } else { // Existing daily report
                currentDailyReport = result.get();
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
     * НА ДАННЫЙ МОМЕНТ МЕТОД НЕ ИСПОЛЬЗУЕТСЯ
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

    /**
     * depending on the status of the chat, we get the status of the bot
     * @param botStatus
     * @return bot status
     */
    private ChatState getBotStatusByLong(Long botStatus) {
        for (ChatState s : ChatState.values()) {
            if(s.equals(botStatus))
                return s;
        }
        return ChatState.DEFAULT;
    }

    /**
     * depending on the update, we use the method of keeping pet
     * @param update
     */
    private void keepingPet(Update update) {
        KeyboardFactory.stageThree();
    }

    /**
     * depending on the update, we use the method consult potential Owner
     * @param update
     */
    private void consultPotentialOwner(Update update) {
        KeyboardFactory.stageTwo();
    }

    /**
     * depending on the update, we use the method consult new user
     * @param update
     */
    private void consultNewUser(Update update) {
        KeyboardFactory.stageOne();
    }

    /**
     * depending on the update, we use the method start buttons
     * @param update
     */
    private void startButtons(Update update) {
        KeyboardFactory.startButtons();
    }

    /**
     * tdepending on the incoming message, choose what to reply to the user
     * @param chatId
     * @param message
     * record an event about a sent message or an error
     */
    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(sendMessage);
        if (response.isOk()) {
            logger.info("message: {} is sent ", message);
        } else {
            logger.warn("Message was not sent. Error code:  " + response.errorCode());
        }
    }

    /**
     * depending on the condition, we give the opportunity to return back to the menu or go to the cattery website
     * @param chatId
     * @param menuHeader
     * @param buttons
     * if the message is not sent record an event about a message sent or an error
     */
    private void sendMenu(Long chatId, String menuHeader, Buttons... buttons) {
        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[buttons.length][1];
        for(int i = 0; i < buttons.length; i++) {
            if(buttons[i].bType.equals(ButtonType.CALLBACK)) {
                inlineKeyboardButtons[i][0] = new InlineKeyboardButton(buttons[i].bText).callbackData(buttons[i].bCallBack);
            } else {
                inlineKeyboardButtons[i][0] = new InlineKeyboardButton(buttons[i].bText).url(buttons[i].url);
            }
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(inlineKeyboardButtons);
        SendMessage message = new SendMessage(chatId, menuHeader);
        message.replyMarkup(inlineKeyboard);
        SendResponse response = telegramBot.execute(message);
        if (response.isOk()) {
            logger.info("menu: {} is sent ", message);
        } else {
            logger.error("Response error: {} {}", response.errorCode(), response.message());
        }
    }

    /**
     * НА ДАННЫЙ МОМЕНТ МЕТОД НЕ ИСПОЛЬЗУЕТСЯ
     * @param update
     * @param message
     * @param keyboardMarkup
     */
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
