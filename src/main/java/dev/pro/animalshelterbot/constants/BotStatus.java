package dev.pro.animalshelterbot.constants;

/**
 * Listing the stages of the user's path
 * It is planned to transfer depending on chatState in the @Entity(name = "chat_config") in PostgreSQL.
 */

public enum BotStatus {
    DEFAULT, CONSULT_NEW_USER, CONSULT_POTENTIAL_OWNER, KEEPING_a_PET, CHAT_WITH_VOLUNTEER
}

