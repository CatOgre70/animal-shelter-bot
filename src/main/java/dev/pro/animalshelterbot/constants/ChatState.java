package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Listing the stages of the user's path
 * It is planned to transfer depending on chatState in the @Entity(name = "chat_config") in PostgreSQL.
 */

public enum ChatState {
    NEW_USER(0L), AWAITING_SHELTER(1L), SHELTER_CHOSEN(2L), DEFAULT(3L), CONSULT_NEW_USER(4L),
    CONSULT_POTENTIAL_OWNER(5L), KEEPING_a_PET(6L), CHAT_WITH_VOLUNTEER(7L),
    AWAITING_GENERAL_WELL_BEING(8L), AWAITING_DIET(9L), AWAITING_CHANGE_IN_BEHAVIOR(10L),
    AWAITING_PHOTO(11L), AWAITING_ADDRESS(12L), AWAITING_PHONE(13L), ZERO_STATE(14L);

    public final Long status;

    private static final Map<Long, ChatState> BY_STATUS = new HashMap<>();

    static {
        for(ChatState bs : values()) {
            BY_STATUS.put(bs.status, bs);
        }
    }
    ChatState(Long status) {
        this.status = status;
    }

    public static ChatState valueOfStatus(Long status) {
        return BY_STATUS.get(status);
    }

}

