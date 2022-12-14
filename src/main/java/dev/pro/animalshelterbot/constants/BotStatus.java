package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Listing the stages of the user's path
 * It is planned to transfer depending on chatState in the @Entity(name = "chat_config") in PostgreSQL.
 */

public enum BotStatus {
    NEW_USER(0L), DEFAULT(1L), CONSULT_NEW_USER(2L), CONSULT_POTENTIAL_OWNER(3L), KEEPING_a_PET(4L), CHAT_WITH_VOLUNTEER(5L);

    public final Long status;

    private static final Map<Long, BotStatus> BY_STATUS = new HashMap<>();

    static {
        for(BotStatus bs : values()) {
            BY_STATUS.put(bs.status, bs);
        }
    }
    BotStatus(Long status) {
        this.status = status;
    }

    public static BotStatus valueOfStatus(Long status) {
        return BY_STATUS.get(status);
    }

}

