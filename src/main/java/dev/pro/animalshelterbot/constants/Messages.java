package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;
/**
 * this class contains a list of responses to correct and incorrect user requests
 */
public enum Messages {
    THERE_IS_NO_SUCH_COMMAND("Команда не распознана. Хотите поговорить с волонтером?"),
    WELCOME_TO_THE_CHATBOT("Добро пожаловать в наш самый распрекрасный чат-бот..."),

    CHOOSE_SHELTER("Выберите, пожалуйста, приют: вы хотите взять собаку или кошку?"),
    CHOOSE_SHELTER1("Нужно выбрать шелтер! Для этого надо нажать одну из двух кнопок в меню ниже"),
    SHELTER_CHOSEN("Спасибо, что вы определились с приютом. Ваш приют: "),
    SHELTER_CHOSEN1(". Для того, чтобы поменять приют, обратитесь, пожалуйста, к нашим волонтерам!"),
    HELP("Используйте, пожалуйста, следующие команды:\n/menu - для вызова меню,\n" +
            "/help - для того, чтобы вывести список доступных команд\n" +
            "/callvolunteers - для того, чтобы связаться с нашими волонтерами"),
    CONTEXT_MENU("Выберите, пожалуйста, действие или введите команду:"),
    GENERAL_WELL_BEING_RECEIVED("Спасибо, я записал информацию об общем состоянии вашего " +
            "питомца в сегодняшний отчет"),
    DIET_RECEIVED("Спасибо, я записал информацию о питании вашего питомца в сегодняшний отчет"),
    CHANGE_IN_BEHAVIOR_RECEIVED("Спасибо, я записал информацию об изменениях в привычках вашего " +
            "питомца в сегодняшний отчет"),
    PHOTO_RECEIVED("Спасибо, я наклеил фотографию вашего питомца на бланк сегодняшнего отчета казеиновым клеем");

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
