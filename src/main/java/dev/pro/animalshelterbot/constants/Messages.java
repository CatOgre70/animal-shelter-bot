package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;

public enum Messages {
    THERE_IS_NO_SUCH_COMMAND("Команда не распознана."),
    WELCOME_TO_THE_CHATBOT("Добро пожаловать в наш самый распрекрасный чат-бот..."),
    HELP("Используйте, пожалуйста, следующие команды:\n/menu - для вызова меню,\n/help - для того, чтобы вывести список доступных команд"),
    CONTEXT_MENU("Выберите, пожалуйста, действие или введите команду:"),
    GENERAL_WELL_BEING_RECEIVED("Спасибо, я записал информацию об общем состоянии вашего питомца в сегодняшний отчет");

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
