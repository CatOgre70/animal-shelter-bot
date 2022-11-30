package dev.pro.animalshelterbot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Chat state class corresponds to the @Entity(name = "chat_config") in PostgreSQL.
 * Model for the ChatConfigsRepository interface
 */
@Entity(name = "chat_config")
public class ChatConfig {

    /**
     * ChatConfigId - Chat configuration identifier, primary key of the chat_config table in PostgreSQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long chatConfigId;

    /**
     * chatId - Chat with user identifier from Telegram
     */
    Long chatId;

    /**
     * chatState - State of chat with user: enum BotStatus
     * { DEFAULT, CONSULT_NEW_USER, CONSULT_POTENTIAL_OWNER, KEEPING_a_PET, CHAT_WITH_VOLUNTEER }
     */
    String chatState;

    /**
     * ChatConfig class empty constructor for Spring JPA and Hibernate
     */
    public ChatConfig() {
        this.chatConfigId = 0L;
        this.chatId = 0L;
        this.chatState = "";
    }

    /**
     * ChatConfig class constructor for using in the AnimalShelterBotApplication
     */
    public ChatConfig(Long chatId, String chatState) {
        this.chatConfigId = 0L;
        this.chatId = chatId;
        this.chatState = chatState;
    }

    public Long getChatConfigId() {
        return chatConfigId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getChatState() {
        return chatState;
    }

    public void setChatState(String chatState) {
        this.chatState = chatState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatConfig that = (ChatConfig) o;
        return Objects.equals(chatConfigId, that.chatConfigId) && Objects.equals(chatId, that.chatId) && Objects.equals(chatState, that.chatState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatConfigId, chatId, chatState);
    }
}
