package dev.pro.animalshelterbot.service;

import dev.pro.animalshelterbot.constants.ChatState;
import dev.pro.animalshelterbot.constants.Shelter;
import dev.pro.animalshelterbot.model.ChatConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatConfigServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ChatConfigService chatConfigService;

    @Test
    public void addChatConfigTest() {
        ChatConfig chatConfig = new ChatConfig(1234567890L, ChatState.NEW_USER, Shelter.DOG_SHELTER);
        chatConfig = chatConfigService.addChatConfig(chatConfig);

        Assertions.assertNotNull(chatConfig);
        Assertions.assertTrue(chatConfig.getId() != 0L);
    }

    @Test
    public void editChatConfigTest() {
        ChatConfig chatConfig = new ChatConfig(1234567890L, ChatState.NEW_USER, Shelter.DOG_SHELTER);
        chatConfig = chatConfigService.addChatConfig(chatConfig);

        Assertions.assertNotNull(chatConfig);
        Assertions.assertTrue(chatConfig.getId() != 0L);
        chatConfig.setChatState(ChatState.AWAITING_SHELTER);
        Optional<ChatConfig> result = chatConfigService.editChatConfig(chatConfig);
        Assertions.assertNotEquals(result, Optional.empty());
        Assertions.assertEquals(chatConfig, result.get());
    }

    @Test
    public void findByChatIdTest() {
        ChatConfig chatConfig = new ChatConfig(1234567890L, ChatState.NEW_USER, Shelter.DOG_SHELTER);
        chatConfig = chatConfigService.addChatConfig(chatConfig);

        Assertions.assertNotNull(chatConfig);
        Assertions.assertTrue(chatConfig.getId() != 0L);

        Optional<ChatConfig> result = chatConfigService.findByChatId(1234567890L);
        Assertions.assertNotEquals(result, Optional.empty());
        Assertions.assertEquals(chatConfig, result.get());

    }

}
