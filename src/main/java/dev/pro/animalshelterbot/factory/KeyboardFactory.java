package dev.pro.animalshelterbot.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import dev.pro.animalshelterbot.constants.Commands;
import dev.pro.animalshelterbot.constants.Constants;

import java.util.Collection;

/**
 * Class that implements the built-in keyboard in the start menu
 */
public class KeyboardFactory {
    public static InlineKeyboardMarkup startButtons() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Узнать информацию о приюте").callbackData(Constants.CHOOSE_OPTION),
                new InlineKeyboardButton("Как взять собаку из приюта").callbackData(Constants.CONSULT_POTENTIAL_OWNER),
                new InlineKeyboardButton("Прислать отчёт о питомце").callbackData(Constants.DAILY_REPORT),
                new InlineKeyboardButton("Позвать волонтера").callbackData("/volunteer"),
                new InlineKeyboardButton("Вернуться в меню").callbackData("/menu")
        );
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup stageOne() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Получить информацию о нашем приюте").callbackData(Constants.ABOUT),
                new InlineKeyboardButton("Получить расписание работы приюта").callbackData(Constants.SCHEDULE),
                new InlineKeyboardButton("Получить адрес приюта и схему проезда").callbackData(Constants.ADDRESS),
                new InlineKeyboardButton("Получить рекомендации по технике безопасности").callbackData(Constants.PRECAUTIONS),
                new InlineKeyboardButton("Передать нам ваши контактные данные").callbackData(Constants.LEAVE_CONTACTS),
                new InlineKeyboardButton("Позвать волонтёра").callbackData("/volunteer"),
                new InlineKeyboardButton("Вернуться в меню").callbackData("/menu")
        );

        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup stageTwo() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Правила знакомства с собакой").callbackData(Constants.DATING_RULES),
                new InlineKeyboardButton("Список необходимых документов").callbackData(Constants.DOCS_LIST),
                new InlineKeyboardButton("Рекомендации по транспортировке животного").callbackData(Constants.TRANSPORTATION),
                new InlineKeyboardButton("Обустройство дома для щенка").callbackData(Constants.PUPPY_IMPROVEMENT),
                new InlineKeyboardButton("Обустройство дома для взрослой собаки").callbackData(Constants.ADULT_IMPROVEMENT),
                new InlineKeyboardButton("Обустройство дома для собаки с ограниченными возможностями").callbackData(Constants.DISABILITIES_IMPROVEMENTS),
                new InlineKeyboardButton("Советы кинолога по первичному общению с собакой").callbackData(Constants.DOG_HANDLERS_TIPS),
                new InlineKeyboardButton("Проверенные кинологи для самостоятельного обращения").callbackData(Constants.PROVEN_DOG_HANDLERS),
                new InlineKeyboardButton("Причины для отказа").callbackData(Constants.REASONS_FOR_REFUSAL),
                new InlineKeyboardButton("Передать нам ваши контактные данные").callbackData(Constants.LEAVE_CONTACTS),
                new InlineKeyboardButton("Позвать волонтёра").callbackData("/volunteer"),
                new InlineKeyboardButton("Вернуться в меню").callbackData("/menu")
        );
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup stageThree() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Прикрепить фото животного").callbackData("Прикрепите фотографию животного размером не более..."),
                new InlineKeyboardButton("Сообщить о рационе, самочувствии и изменениях в поведении питомца").callbackData("Напишите информацию о дневном рационе, самочувствии и изменениях в поведении питомца одним сообщением ниже: ")
        );
        return inlineKeyboard;
    }
}
