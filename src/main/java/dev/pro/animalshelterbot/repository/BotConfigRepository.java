package dev.pro.animalshelterbot.repository;

import dev.pro.animalshelterbot.model.BotConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BotConfigRepository is the interface to storage for Telegram bot configuration data.
 * Corresponds to the bot_config table in PostgreSQL. Extends {@link JpaRepository}
 * @see BotConfig BotConfig
 */
@Repository
public interface BotConfigRepository extends JpaRepository<BotConfig, Long> {

    /**
     * @return findAll() method returns all available Telegram bot configurations from the database.
     * AnimalShelterBot application uses only first row in this list
     */
    List<BotConfig> findAll();
}
