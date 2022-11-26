package dev.pro.animalshelterbot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import dev.pro.animalshelterbot.constants.BotStatus;
import dev.pro.animalshelterbot.constants.Constants;


// Продумать реализацию пропихивания
/**
 Class that implements the built-in keyboard in the start menu
 */
public class KeyboardFactory {
    public static InlineKeyboardMarkup startButtons() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                //Приветствуем пользователя и пропихиваем его как-то на BotStatus.CONSULT_NEW_USER - либо отдельный класс для этапа, либо в Message
                new InlineKeyboardButton("Узнать информацию о приюте").callbackData(Constants.GREETINGS),
                //Снова приветствуем - так в ТЗ))) и пропихиваем на BotStatus.CONSULT_POTENTIAL_OWNER - либо отдельный класс для этапа, либо в MessageFactory
                new InlineKeyboardButton("Как взять собаку из приюта").callbackData(Constants.GREETINGS),
                //Здесь, наверное, д.б. вызов не enum, а класса, либо метода, отвечающих за этап
                new InlineKeyboardButton("Прислать отчёт о питомце").callbackData(Constants.CHOOSE_OPTION),
                //В ТЗ данная фича никак не прописана - пока вообще непонятно как реализовывать передачу сообщения ботом какому-то конкретному кругу лиц и обратно
                new InlineKeyboardButton("Позвать волонтера").callbackData(BotStatus.CHAT_WITH_VOLUNTEER.name())
        );
        return inlineKeyboard;
    }
}