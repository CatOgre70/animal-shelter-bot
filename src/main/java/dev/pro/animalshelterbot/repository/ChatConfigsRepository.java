package dev.pro.animalshelterbot.repository;

import model.ChatConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ChatConfigsRepository is the interface to storage for Telegram bot chat statuses.
 * Corresponds to the chat_config table in PostgreSQL. Extends {@link JpaRepository}
 * @see ChatConfig ChatConfig
 */
@Repository
public interface ChatConfigsRepository extends JpaRepository<ChatConfig, Long> {
}
