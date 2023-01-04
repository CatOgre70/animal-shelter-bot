package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import dev.pro.animalshelterbot.constants.Messages;
import dev.pro.animalshelterbot.service.AnimalService;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.DailyReportService;
import dev.pro.animalshelterbot.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TelegramBotUpdatesListenerTest {

    @Mock
    private ChatConfigService chatConfigService;

    @Mock
    private DailyReportService dailyReportService;

    @Mock
    private UserService userService;

    @Mock
    private AnimalService animalService;

    @MockBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @LocalServerPort
    private int port;

    @Test
    public void commandsTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(TelegramBotUpdatesListenerTest.class.getResource("message_update.json").toURI()));
        Update update = getUpdate(json, "/start");
        telegramBotUpdatesListener.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, Mockito.times(3)).execute(argumentCaptor.capture());
        List<SendMessage> actuals = argumentCaptor.getAllValues();

        Assertions.assertThat(actuals).hasSize(3);
        SendMessage actual = actuals.get(0);

        Map<String, Object> actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo(Messages.WELCOME_TO_THE_CHATBOT.messageText);

        actual = actuals.get(1);

        actualParameters = actual.getParameters();
        Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actualParameters.get("text")).isEqualTo(Messages.CHOOSE_SHELTER.messageText);

        // todo: check sendMessage in sendMenu
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%text%", replaced), Update.class);
    }

}
