package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import dev.pro.animalshelterbot.constants.AnimalKind;
import dev.pro.animalshelterbot.constants.ChatState;
import dev.pro.animalshelterbot.constants.Messages;
import dev.pro.animalshelterbot.constants.Shelter;
import dev.pro.animalshelterbot.menu.ButtonType;
import dev.pro.animalshelterbot.menu.Buttons;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.service.AnimalService;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.DailyReportService;
import dev.pro.animalshelterbot.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TelegramBotUpdatesListenerTest {

    @MockBean
    private ChatConfigService chatConfigService;

    @MockBean
    private DailyReportService dailyReportService;

    @MockBean
    private UserService userService;

    @MockBean
    private AnimalService animalService;

    @MockBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @LocalServerPort
    private int port;

    @Test
    public void startCommandInTheStartTest() throws URISyntaxException, IOException {

        // /start command in the chat status ChatState.NEW_USER

        String json = Files.readString(Paths.get(TelegramBotUpdatesListenerTest.class.getResource("message_update.json").toURI()));
        Update update = getUpdate(json, "/start");
        telegramBotUpdatesListener.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, Mockito.times(3)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();

        Assertions.assertThat(actualList).hasSize(3);
        SendMessage actual = actualList.get(0);

        Map<String, Object> actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo(Messages.WELCOME_TO_THE_CHATBOT.messageText);

        actual = actualList.get(1);

        actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo(Messages.CHOOSE_SHELTER.messageText);

        // todo: check sendMessage in sendMenu

        actual = actualList.get(2);
        actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo("Приюты");
        SendMessage expected = sendMenu(1234567890L, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
        Assertions.assertThat(actualParameters.get("reply_markup"))
                .isEqualTo(expected.getParameters().get("reply_markup"));
    }

    @Test
    public void startCommandInTheAwaitingShelterTest() throws URISyntaxException, IOException {
        // /start command in the chat status ChatState.AWAITING_SHELTER
        // Database objects initialization
        Animal animal = new Animal("Sharik", AnimalKind.DOG, "Cur", "Brown",
                "Любит жрать тухлую селедку с помойки, но слаб желудком, не позволять ни в коем случае!",
                null, 0L, null,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0)
                        .truncatedTo(ChronoUnit.DAYS), null);
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user.setId(1L);
        animal.setOwner(user);
        animal.setShelter(Shelter.DOG_SHELTER);
        animal.setId(1L);
        ChatConfig chatConfig = new ChatConfig(1234567890L, ChatState.AWAITING_SHELTER, null);
        chatConfig.setId(1L);

        // Mocking chatConfigService.findByChatId() method
        when(chatConfigService.addChatConfig(any(ChatConfig.class))).thenReturn(chatConfig);
        when(chatConfigService.editChatConfig(any(ChatConfig.class))).thenReturn(Optional.of(chatConfig));
        when(chatConfigService.findByChatId(any(Long.class))).thenReturn(Optional.of(chatConfig));
        when(userService.findByChatId(any(Long.class))).thenReturn(Optional.of(user));


        // Sending /start command and analysing the result
        String json = Files.readString(Paths.get(TelegramBotUpdatesListenerTest.class.getResource("message_update.json").toURI()));
        Update update = getUpdate(json, "/start");
        telegramBotUpdatesListener.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, Mockito.times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();

        Assertions.assertThat(actualList).hasSize(2);
        SendMessage actual = actualList.get(0);

        Map<String, Object> actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo(Messages.CHOOSE_SHELTER1.messageText);

        actual = actualList.get(1);
        actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo("Приюты");
        SendMessage expected = sendMenu(1234567890L, "Приюты", Buttons.DOG_SHELTER, Buttons.CAT_SHELTER);
        Assertions.assertThat(actualParameters.get("reply_markup"))
                .isEqualTo(expected.getParameters().get("reply_markup"));
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%text%", replaced), Update.class);
    }

    private SendMessage sendMenu(Long chatId, String menuHeader, Buttons... buttons) {
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
        return message;
    }

}
