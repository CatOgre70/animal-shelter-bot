package dev.pro.animalshelterbot.constants;

// Дозаполнить descriptions для команд 2го этапа
// Прописать команды 3го этапа и продумать их реализацию. Или наоборот, сначала продумать, потом прописать..

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing all commands available to the user, depending on the stage of the user's path
 * Used in @see TelegramBotUpdatesListener
 */

public enum Commands{

    START("/start"),
    HELP("/help"),
    MENU("/menu"),
    ABOUT("/about"),
    SCHEDULE("/work schedule"),
    ADDRESS("/address and driving directions"),
    PRECAUTIONS("/safety precautions"),
    LEAVE_CONTACTS("/leave contacts"),
    CALL_VOLUNTEER("/volunteer"),
    DATING_RULES("/dating rules"),
    DOCS_LIST("/docs list"),
    TRANSPORTATION("/transportation recommendations"),
    PUPPY_IMPROVEMENT("/puppy home improvement"),
    ADULT_IMPROVEMENT("/adult home improvement"),
    DISABILITIES_IMPROVEMENTS("/with disabilities home improvement"),
    DOG_HANDLERS_TIPS("/tips from a dog handler"),
    PROVEN_DOG_HANDLERS("/proven dog handlers"),
    REASONS_FOR_REFUSAL("/reasons for refusals"),
    CONSULT_NEW_USER("/consult new user"),

    CONSULT_POTENTIAL_OWNER("/consult potential owner"),

    DAILY_REPORT("/send daily report");



    public final String s;


    public final String commandText;

    Commands (String s) {
        this.commandText = s;
    }

    private static Map<String, Commands> BY_TEXT = new HashMap<>();

    static {
        for(Commands c : values()) {
            BY_TEXT.put(c.commandText, c);
        }
    }

    public static Commands valueOfCommandText(String cText) {
        return BY_TEXT.get(cText);
    }

    /**
     * Commands available to the user on stage CONSULT_NEW_USER
     */

    public enum consultNewUser {

        ABOUT("/about", "Получить информацию о нашем приюте"),
        SCHEDULE("/work schedule", "Получить расписание работы приюта"),
        ADDRESS("/address and driving directions", "Получить адрес приюта и схему проезда"),
        PRECAUTIONS("/safety precautions","Получить общие рекомендации по технике безопасности на территории приюта"),
        LEAVE_CONTACTS("/leave contacts", "Передать нам ваши контактные данные"),
        CALL_VOLUNTEER("/volunteer", "Позвать волонтёра")
        ;

        consultNewUser(String s, String description) {

        }
    }

    // Дозаполнить descriptions
    /**
     * Commands available to the user on stage CONSULT_POTENTIAL_OWNER
     */

    public enum consultPotentialOwner {

        DATING_RULES("/dating rules","Получить правила знакомства с собакой"),
        DOCS_LIST("/docs list", "Получить список необходимых документов"),
        TRANSPORTATION("/transportation recommendations", "Получить список рекомендаций по транспортировке " +
                "животного"),
        PUPPY_IMPROVEMENT("/puppy home improvement", "Получить список рекомендаций по обустройству дома " +
                "для щенка"),
        ADULT_IMPROVEMENT("/adult home improvement", "Получить список рекомендаций по обустройству дома для " +
                "взрослой собаки."),
        DISABILITIES_IMPROVEMENTS("/with disabilities home improvement", "список рекомендаций по " +
                "обустройству дома для собаки с ограниченными возможностями"),
        DOG_HANDLERS_TIPS("/tips from a dog handler", "Получить советы кинолога по первичному общению с " +
                "собакой"),
        PROVEN_DOG_HANDLERS("/proven dog handlers", "Получить рекомендации по проверенным кинологам для " +
                "дальнейшего обращения к ним"),
        REASONS_FOR_REFUSAL("/reasons for refusals", "Получить список причин отказа в адаптации животного"),
        LEAVE_CONTACTS("/leave contacts", "Передать нам ваши контактные данные"),
        CALL_VOLUNTEER("/volunteer", "Позвать волонтёра")
        ;

        consultPotentialOwner(String s, String description){

    }
    }

    public enum KEEPING_a_PET {
        DAILY_REPORT,
        REQUEST_TEXT,
        REQUEST_PHOTO,
        REPORT_WARNING,
        CONGRATS,
        EXTRA_TIME,
        NOT_PASS,
        CALL_VOLUNTEER
    }
}
