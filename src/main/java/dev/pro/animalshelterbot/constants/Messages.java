package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;
/**
 * this class contains a list of responses to correct and incorrect user requests
 */
public enum Messages {
    THERE_IS_NO_SUCH_COMMAND("Команда не распознана."),
    WELCOME_TO_THE_CHATBOT("Добро пожаловать в наш самый распрекрасный чат-бот..."),

    CHOOSE_SHELTER("Выберите, пожалуйста, приют: вы хотите взять собаку или кошку?"),
    CHOOSE_SHELTER1("Сначала необходимо выбрать приют! Для этого надо нажать одну из двух кнопок в меню ниже"),
    SHELTER_CHOSEN("Спасибо, что вы определились с приютом. Ваш приют: "),
    SHELTER_CHOSEN1(". Для того, чтобы поменять приют, вернитесь, пожалуйста, к предыдущему меню!"),
    HELP("Используйте, пожалуйста, следующие команды:\n" +
            "/menu - для вызова контекстного меню,\n" +
            "/up - для того, чтобы перейти к предыдущему меню\n" +
            "/help - для того, чтобы вывести список доступных команд\n" +
            "/callvolunteers - для того, чтобы связаться с нашими волонтерами\n" +
            "/tobevolunteer - если вы хотите стать нашим волонтером\n" +
            "А еще можно просто выбирать соответствующие пункты контекстного меню"),
    CONTEXT_MENU("Выберите, пожалуйста, действие или введите команду.\n" +
            "/help - для списка команд\n/menu - для повторного вывода меню"),

    DAILY_REPORT_MENU_WELCOME("Отправка ежедневного отчета о питомце " +
            "(30 дней с того дня, когда вы забрали его из приюта)"),

    SEND_GENERAL_WELL_BEING("Пришлите, пожалуйста, сообщение с описанием общего самочувствия вашего питомца ->"),

    GENERAL_WELL_BEING_RECEIVED("Спасибо, я записал информацию об общем состоянии вашего " +
            "питомца в сегодняшний отчет"),

    SEND_DIET("Пришлите, пожалуйста, сообщение с описанием диеты вашего питомца ->"),
    DIET_RECEIVED("Спасибо, я записал информацию о питании вашего питомца в сегодняшний отчет"),

    SEND_CHANGE_IN_BEHAVIOR("Пришлите, пожалуйста, сообщение с описанием изменений в привычках вашего питомца ->"),
    CHANGE_IN_BEHAVIOR_RECEIVED("Спасибо, я записал информацию об изменениях в привычках вашего " +
            "питомца в сегодняшний отчет"),

    SEND_PHOTO("Пришлите, пожалуйста, фотографию вашего питомца ->"),
    PHOTO_RECEIVED("Спасибо, я наклеил фотографию вашего питомца на бланк сегодняшнего отчета казеиновым клеем"),

    WRONG_COMMAND("Эта команда здесь не работает. Попробуйте, пожалуйста, другую"),

    DOG_SHELTER_GENERAL_INFO("Это приют для собак, основанный неравнодушными людьми нашего города... " +
            "Подробнее: https://www.instagram.com/asenka_astana/"),
    CAT_SHELTER_GENERAL_INFO("Это приют для кошек, основанный неравнодушными людьми нашего города... " +
            "Подробнее: https://www.instagram.com/asenka_astana/"),

    DOG_ANIMAL_WELCOME("Ну как?! Подходишь и бац! Знакомишься! Подробнее: https://www.instagram.com/asenka_astana/"),
    CAT_ANIMAL_WELCOME("Ну как?! Подходишь и бац! Знакомишься! Подробнее: https://www.instagram.com/asenka_astana/");


    public final String messageText;

    Messages(String str) {
        this.messageText = str;
    }

    public static Map<String, Messages> BY_MESSAGE_TEXT = new HashMap<>();

    static {
        for(Messages s : values()) {
            BY_MESSAGE_TEXT.put(s.messageText, s);
        }
    }

    public static Messages valueOfMessageText(String messageText){
        return BY_MESSAGE_TEXT.get(messageText);
    }

}
