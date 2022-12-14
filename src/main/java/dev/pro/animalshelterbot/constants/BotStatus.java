package dev.pro.animalshelterbot.constants;

/**
 * Listing the stages of the user's path
 * It is planned to transfer depending on chatState in the @Entity(name = "chat_config") in PostgreSQL.
 */

public enum BotStatus {
    DEFAULT(0L), CONSULT_NEW_USER(1L), CONSULT_POTENTIAL_OWNER(2L), KEEPING_a_PET(3L), CHAT_WITH_VOLUNTEER(4L);

    public final Long status;
    BotStatus(Long status) {
        this.status = status;
    }

}

