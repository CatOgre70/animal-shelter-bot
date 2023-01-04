package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import dev.pro.animalshelterbot.constants.ChatState;
import dev.pro.animalshelterbot.constants.Messages;
import dev.pro.animalshelterbot.constants.Shelter;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.repository.ChatConfigRepository;
import dev.pro.animalshelterbot.service.AnimalService;
import dev.pro.animalshelterbot.service.ChatConfigService;
import dev.pro.animalshelterbot.service.DailyReportService;
import dev.pro.animalshelterbot.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @LocalServerPort
    private int port;

    @Test
    public void commandsTest() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get("src/test/resources/dev.pro.animalshelterbot/listener/message_update.json"));
        json = Files.readString(Paths.get(TelegramBotUpdatesListenerTest.class.getResource("message_update.json").toURI()));
        Update update = getUpdate(json, "/start");
        telegramBotUpdatesListener.process(Collections.singletonList(update));

        Mockito.verify(telegramBot).execute(Mockito.argThat(argument -> {
            Map<String, Object> actualParameters = argument.getParameters();
            Assertions.assertThat(actualParameters.get("chat_id")).isEqualTo(1234567890L);
            Assertions.assertThat(actualParameters.get("text")).isEqualTo(Messages.WELCOME_TO_THE_CHATBOT.messageText);
            return true;
        }));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(Messages.WELCOME_TO_THE_CHATBOT.messageText);

    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%text%", replaced), Update.class);
    }

}
