package dev.pro.animalshelterbot.constants;

// Дозаполнить descriptions для команд 2го этапа
// Прописать команды 3го этапа и продумать их реализацию. Или наоборот, сначала продумать, потом прописать..
/**
 * Class containing all commands available to the user, depending on the stage of the user's path
 * Used in @see TelegramBotUpdatesListener
 */

public enum Commands{

    START("/start", "Начать работу с ботом"),

    HELP("/help", "Получить список команд"),
    MENU("/menu", "Вызвать меню"),

    //Эти команды повторяются на 1м и втором этапах, нужно не забыть их туда включить, или сразу задублировать код ниже
    LEAVE_CONTACTS("/leave contacts", "Передать нам ваши контактные данные"),
    //Эта ещё и на 3м этапе
    CALL_VOLUNTEER("/volunteer", "Позвать волонтёра")
    ;

    Commands (String s, String description) {

    }

    /**
     * Commands available to the user on stage CONSULT_NEW_USER
     */

    public enum consultNewUser {

        ABOUT("/about", "Получить информацию о нашем приюте"),
        SCHEDULE("/work schedule", "Получить расписание работы приюта"),
        ADDRESS("/address and driving directions", "Получить адрес приюта и схему проезда"),
        PRECAUTIONS("/safety precautions","Получить общие рекомендации по технике безопасности на территории приюта"),
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
        TRANSPORTATION("/transportation recommendations", ""),
        PUPPY_IMPROVEMENT("/puppy home improvement", ""),
        ADULT_IMPROVEMENT("/adult home improvement", ""),
        DISABILITIES_IMPROVEMENTS("/with disabilities home improvement", ""),
        DOG_HANDLERS_TIPS("/tips from a dog handler", ""),
        PROVEN_DOG_HANDLERS("/proven dog handlers", ""),
        REASONS_FOR_REFUSAL("/reasons for refusals", "")
        ;


        consultPotentialOwner(String s, String description){

    }
    }


}
