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
import dev.pro.animalshelterbot.constants.*;
import dev.pro.animalshelterbot.exception.UserNotFoundException;
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
            // Processing updates
            logger.info("Processing update: {}", update);
            Long chatId = 0L;
            String firstName = null, lastName = null, nickName = null;
            ChatState chatState;

            UpdateType updateType = checkingUpdate(update);
            if(updateType == UpdateType.COMMAND || updateType == UpdateType.MESSAGE || updateType == UpdateType.PHOTO) {
                chatId = update.message().from().id();
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
            // Checking that user is volunteer or not
            if(user.isVolunteer()) {
                sendMessage(chatId, Messages.YOU_ARE_VOLUNTEER_NOW1.messageText + "@" + user.getNickName()
                        + Messages.YOU_ARE_VOLUNTEER_NOW2);
                return;
            }

            switch(chatState) {
                case NEW_USER:
                    newUserMenu(chatId, chatConfig);
                    break;
                case AWAITING_SHELTER:
                    awaitingShelterStatusUpdateProcessing(chatId, update, updateType, user, chatConfig);
                    break;
                case SHELTER_CHOSEN:
                case CHAT_WITH_VOLUNTEER:
                    break;
                case DEFAULT:
                    defaultStatusUpdateProcessing(chatId, update, updateType, user, chatConfig);
                    break;
                case CONSULT_NEW_USER:
                    consultNewUserStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case CONSULT_POTENTIAL_OWNER:
                    consultPotentialOwnerStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case KEEPING_a_PET:
                    keepingPetStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case AWAITING_GENERAL_WELL_BEING:
                    awaitingGeneralWellBeingStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case AWAITING_DIET:
                    awaitingDietStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case AWAITING_CHANGE_IN_BEHAVIOR:
                    awaitingChangeInBehaviorStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case AWAITING_PHOTO:
                    awaitingPhotoStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case AWAITING_ADDRESS:
                    awaitingAddressStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
                case AWAITING_PHONE:
                    awaitingPhoneStatusUpdateProcessing(chatId, update, updateType, chatConfig);
                    break;
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendSignalToVolunteers(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {

    }

    private void awaitingAddressStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
        if(updateType == UpdateType.COMMAND) {
            // We are not processing commands on this stage
            sendMessage(chatId, Messages.SEND_ADDRESS.messageText);
        } else if(updateType == UpdateType.MESSAGE) {
            // Processing message as address update
            Optional<User> userResult = userService.findByChatId(chatId);
            if(userResult.isEmpty()) { // user with such chatId was not found in the database
                logger.error("User with such chatId was not found in the database");
                throw new UserNotFoundException("User with such chatId was not found in the database");
            } else { // Existing user
                User user =  userResult.get();
                user.setAddress(update.message().text());
                userService.editUser(user);
            }
            chatConfig.setChatState(chatConfig.getPreviousChatState());
            chatConfig.setPreviousChatState(ChatState.ZERO_STATE);
            chatConfigService.editChatConfig(chatConfig);
            sendMessage(chatId, Messages.ADDRESS_SENT.messageText);
        } else if(updateType == UpdateType.PHOTO) {
            // We are not processing photos at this stage
            sendMessage(chatId, Messages.SEND_ADDRESS.messageText);
        } else if(updateType == UpdateType.CALL_BACK_QUERY) {
            // We are not processing callback queries at this stage
            sendMessage(chatId, Messages.SEND_ADDRESS.messageText);
        } else {
            logger.error("Unrecognized update!");
        }

    }

    private void awaitingPhoneStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
        if(updateType == UpdateType.COMMAND) {
            // We are not processing commands on this stage
            sendMessage(chatId, Messages.SEND_PHONE.messageText);
        } else if(updateType == UpdateType.MESSAGE) {
            // Processing message as a phone number update
            Optional<User> userResult = userService.findByChatId(chatId);
            if(userResult.isEmpty()) { // user with such chatId was not found in the database
                logger.error("User with such chatId was not found in the database");
                throw new UserNotFoundException("User with such chatId was not found in the database");
            } else { // Existing user
                User user =  userResult.get();
                user.setMobilePhone(update.message().text());
                userService.editUser(user);
            }
            chatConfig.setChatState(ChatState.AWAITING_ADDRESS);
            chatConfigService.editChatConfig(chatConfig);
            sendMessage(chatId, Messages.PHONE_SENT.messageText);
            sendMessage(chatId, Messages.SEND_ADDRESS.messageText);
        } else if(updateType == UpdateType.PHOTO) {
            // We are not processing photos at this stage
            sendMessage(chatId, Messages.SEND_PHONE.messageText);
        } else if(updateType == UpdateType.CALL_BACK_QUERY) {
            // We are not processing callback queries at this stage
            sendMessage(chatId, Messages.SEND_PHONE.messageText);
        } else {
            logger.error("Unrecognized update!");
        }

    }

    private void awaitingPhotoStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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

    }

    private void awaitingChangeInBehaviorStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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

    }

    private void awaitingDietStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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

    }

    private void awaitingGeneralWellBeingStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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

    }

    private void keepingPetStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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
                    Buttons.DR_DIET, Buttons.DR_CHANGE_IN_BEHAVIOR, Buttons.DR_PHOTO, Buttons.DR_STATUS,
                    Buttons.HELP, Buttons.CALL_VOLUNTEER);
        } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                update.callbackQuery().data().equals(Buttons.DR_STATUS.bCallBack)) {
            Optional<DailyReport> dailyReportResult = dailyReportService.findDailyReportByChatId(chatId);
            String drPhoto = "не отправлена";
            String drGeneralWellBeing = "не отправлен";
            String drDiet = "не отправлен";
            String drChangeInBehavior = "не отправлен";
            if(dailyReportResult.isPresent() && dailyReportResult.get().getGeneralWellBeing() != null) {
                drGeneralWellBeing = "отправлен";
            }
            if(dailyReportResult.isPresent() && dailyReportResult.get().getDiet() != null) {
                drDiet = "отправлен";
            }
            if(dailyReportResult.isPresent() && dailyReportResult.get().getChangeInBehavior() != null) {
                drChangeInBehavior = "отправлен";
            }
            if(dailyReportResult.isPresent() && dailyReportResult.get().getFilePath() != null) {
                drPhoto = "отправлена";
            }
            sendMessage(chatId, "Состояние сегодняшнего отчета:\nОтчет об общем самочувствии питомца - "
                    + drGeneralWellBeing + "\nОтчет о диете питомца - " + drDiet + "\nОтчет об изменениях в привычках - "
                    + drChangeInBehavior + "\nФотография питомца - " + drPhoto);
        } else {
            logger.error("Unrecognized update!");
        }
    }

    private void consultPotentialOwnerStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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
            Shelter shelter = chatConfig.getShelter();
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
    }

    private void consultNewUserStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, ChatConfig chatConfig) {
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
            Shelter shelter = chatConfig.getShelter();
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
        } else if (updateType == UpdateType.CALL_BACK_QUERY &&
                update.callbackQuery().data().equals(Buttons.SEND_PHONE_AND_ADDRESS.bCallBack)) {
            // Processing send phone and address button
            sendMessage(chatId, Messages.SEND_PHONE.messageText);
            chatConfig.setPreviousChatState(ChatState.CONSULT_NEW_USER);
            chatConfig.setChatState(ChatState.AWAITING_PHONE);
            chatConfigService.editChatConfig(chatConfig);
        } else {
            logger.error("Unrecognized update!");
        }
    }

    private void defaultStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType, User user, ChatConfig chatConfig) {
        Shelter shelter;
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
            Optional<User> userResult = userService.findByChatId(chatId);
            if(userResult.isEmpty()) {
                logger.error("User with such chat id was not found in the database!");
                throw new UserNotFoundException("User with such chat id was not found in the database!");
            } else if(userResult.get().getAdoptedAnimal() == null) {
                sendMessage(chatId, Messages.YOU_HAVE_NOT_ADOPTED_ANIMAL.messageText);
                return;
            } else if(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30)
                    .isAfter(userResult.get().getAdoptedAnimal().getAdoptionDate())) {
                sendMessage(chatId, Messages.YOU_DONT_NEED_TO_SEND_DAILY_REPORT.messageText);
                return;
            }
            chatConfig.setChatState(ChatState.KEEPING_a_PET);
            chatConfigService.editChatConfig(chatConfig);
            sendMessage(chatId, Messages.DAILY_REPORT_MENU_WELCOME.messageText);
            sendMessage(chatId, Messages.CONTEXT_MENU.messageText);
            sendMenu(chatId, "Части отчета", Buttons.BACK, Buttons.DR_GENERAL_WELL_BEING,
                    Buttons.DR_DIET, Buttons.DR_CHANGE_IN_BEHAVIOR, Buttons.DR_PHOTO, Buttons.DR_STATUS,
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
        } else if((updateType == UpdateType.CALL_BACK_QUERY &&
                update.callbackQuery().data().equals(Buttons.CALL_VOLUNTEER.bCallBack)) || (updateType == UpdateType.COMMAND &&
                update.message().text().equalsIgnoreCase(Commands.CALL_VOLUNTEERS.commandText))) {
            sendSignalToVolunteers(chatId, update, updateType, chatConfig);
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
    }

    private void awaitingShelterStatusUpdateProcessing(Long chatId, Update update, UpdateType updateType,
                                                       User user, ChatConfig chatConfig) {
        Shelter shelter;
        if(updateType == UpdateType.CALL_BACK_QUERY &&
                update.callbackQuery().data().equals(Buttons.DOG_SHELTER.bCallBack)) {
            shelter = Shelter.DOG_SHELTER;
        } else if(updateType == UpdateType.CALL_BACK_QUERY &&
                update.callbackQuery().data().equals(Buttons.CAT_SHELTER.bCallBack)) {
            shelter = Shelter.CAT_SHELTER;
        } else {
            sendMessage(chatId, Messages.CHOOSE_SHELTER1.messageText);
            sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
            return;
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
    }

    /**
     * Sending menu for new user and changing chatState to ChatState.AWAITING_SHELTER
     * @param chatId - chat id
     * @param chatConfig - chat configuration
     */
    private void newUserMenu(Long chatId, ChatConfig chatConfig) {
        sendMessage(chatId, Messages.WELCOME_TO_THE_CHATBOT.messageText);
        sendMessage(chatId, Messages.CHOOSE_SHELTER.messageText);
        sendMenu(chatId, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
        chatConfig.setChatState(ChatState.AWAITING_SHELTER);
        chatConfigService.editChatConfig(chatConfig);
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

        } else if (update.callbackQuery() != null) { // Callback answer processing
            return UpdateType.CALL_BACK_QUERY;

        } else {
            return UpdateType.ERROR;
        }
    }

    /**
     * tdepending on the incoming message, choose what to reply to the user
     * @param chatId chat id
     * @param message message text to send
     * record an event about sent message or an error
     */
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

    /**
     * depending on the condition, we give the opportunity to return back to the menu or go to the cattery website
     * @param chatId chat id
     * @param menuHeader menu header
     * @param buttons buttons
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
        if (response != null && response.isOk()) {
            logger.info("menu: {} is sent ", message);
        } else if (response != null){
            logger.error("Response error: {} {}", response.errorCode(), response.message());
        } else {
            logger.error("Response error: response is null");
        }
    }
}