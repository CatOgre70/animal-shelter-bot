package dev.pro.animalshelterbot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import dev.pro.animalshelterbot.constants.Constants;

/**
 Class that implements the built-in keyboard in the start menu
 */
public class KeyboardFactory {
    public static InlineKeyboardMarkup startButtons() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Узнать информацию о приюте").callbackData(Constants.CONSULT_NEW_USER),
                new InlineKeyboardButton("Как взять собаку из приюта").callbackData(Constants.CONSULT_POTENTIAL_OWNER),
                new InlineKeyboardButton("Прислать отчёт о питомце").callbackData(Constants.DAILY_REPORT),
                new InlineKeyboardButton("Позвать волонтера").callbackData(Constants.CALL_VOLUNTEER)
        );
        return inlineKeyboard;
    }
}