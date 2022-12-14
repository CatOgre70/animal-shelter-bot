package dev.pro.animalshelterbot.constants;

// Дозаполнить descriptions для команд 2го этапа
// Прописать команды 3го этапа и продумать их реализацию. Или наоборот, сначала продумать, потом прописать..
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


    Commands (String s) {
        this.s = s;
    }

    //Этот метод возвращает строковое значение, хранящееся в Enum.
    public String getS() {
        return s;
    }

    //Этот метод наоборот из строчного значения возвращает Enum
    public static Commands valueOfS(String s) {
        for (Commands c : values()) {
            if (c.s.equals(s)) {
                return c;
            }
        }
       return HELP;
    }
}









