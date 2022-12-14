package dev.pro.animalshelterbot.service;

import dev.pro.animalshelterbot.constants.BotStatus;
import dev.pro.animalshelterbot.model.ChatConfig;
import dev.pro.animalshelterbot.repository.ChatConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatConfigService {

    private final ChatConfigRepository chatConfigRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ChatConfigService (ChatConfigRepository chatConfigRepository) {
        this.chatConfigRepository = chatConfigRepository;
    }

    public ChatConfig addChatConfig(ChatConfig chatConfig) {
        logger.info("Method \"ChatConfigService.addChatConfig()\" was called");
        return chatConfigRepository.save(chatConfig);
    }

    public ChatConfig editChatConfig(ChatConfig chatConfig) {
        logger.info("Method \"ChatConfigService.editChatConfig()\" was called");
        Optional<ChatConfig> result = chatConfigRepository.findById(chatConfig.getChatId());
        if(!result.isPresent()) {
            return null;
        }
        else {
            ChatConfig fromDb = result.get();
            fromDb.setChatState(chatConfig.getChatState());
            return chatConfigRepository.save(fromDb);
        }
    }

    public ChatConfig findByChatId(Long chatId) {
        logger.info("Method \"ChatConfigService.findByChatId()\" was called");
        return chatConfigRepository.findByChatId(chatId);
    }

}
