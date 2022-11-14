package model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Long chatId;

    Long chatState;

    public ChatConfig() {
        this.chatId = 0L;
        this.chatState = 0L;
    }

    public ChatConfig(Long chatId, Long chatState) {
        this.chatId = chatId;
        this.chatState = chatState;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatState() {
        return chatState;
    }

    public void setChatState(Long chatState) {
        this.chatState = chatState;
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
