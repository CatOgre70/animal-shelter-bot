package dev.pro.animalshelterbot.model;

import dev.pro.animalshelterbot.constants.ChatState;
import dev.pro.animalshelterbot.constants.Shelter;

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
    private Long id;

    /**
     * chatId - Chat with user identifier from Telegram
     */
    private Long chatId;

    /**
     * chatState - State of chat with user: enum BotStatus
     * { DEFAULT, CONSULT_NEW_USER, CONSULT_POTENTIAL_OWNER, KEEPING_a_PET, CHAT_WITH_VOLUNTEER }
     */
    private ChatState chatState;

    private Shelter shelter;

    /**
     * ChatConfig class empty constructor for Spring JPA and Hibernate
     */
    public ChatConfig() {
        this.id = 0L;
        this.chatId = 0L;
        this.chatState = null;
    }

    /**
     * ChatConfig class constructor for using in the AnimalShelterBotApplication
     */
    public ChatConfig(Long chatId, ChatState chatState, Shelter shelter) {
        this.id = 0L;
        this.chatId = chatId;
        this.chatState = chatState;
        this.shelter = shelter;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public ChatState getChatState() {
        return chatState;
    }

    public void setChatState(ChatState chatState) {
        this.chatState = chatState;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatConfig that = (ChatConfig) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(chatState, that.chatState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, chatState);
    }
}
