package dev.pro.animalshelterbot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Telegram bot configuration class corresponds to the @Entity(name = "bot_config") in PostgreSQL.
 * Model for BotConfigsRepository interface
 */
@Entity (name = "bot_config")
public class BotConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long botConfigId;

    private String botName;

    private String accessToken;

    private String telegramCallbackAnswerTemp;

    public BotConfig() {
        this.botConfigId = 0L;
        this.botName = null;
        this.accessToken = null;
        this.telegramCallbackAnswerTemp = null;
    }

    public BotConfig(String botName, String accessToken, String telegramCallbackAnswerTemp) {
        this.botName = botName;
        this.accessToken = accessToken;
        this.telegramCallbackAnswerTemp = telegramCallbackAnswerTemp;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTelegramCallbackAnswerTemp() {
        return telegramCallbackAnswerTemp;
    }

    public void setTelegramCallbackAnswerTemp(String telegramCallbackAnswerTemp) {
        this.telegramCallbackAnswerTemp = telegramCallbackAnswerTemp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotConfig botConfig = (BotConfig) o;
        return Objects.equals(botConfigId, botConfig.botConfigId) && Objects.equals(botName, botConfig.botName) && Objects.equals(accessToken, botConfig.accessToken) && Objects.equals(telegramCallbackAnswerTemp, botConfig.telegramCallbackAnswerTemp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(botConfigId, botName, accessToken, telegramCallbackAnswerTemp);
    }
}
