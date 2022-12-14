package dev.pro.animalshelterbot.constants;

// Прописать ещё константы согласно ТЗ

import dev.pro.animalshelterbot.service.UserService;

/**
 * An interface used for storing and passing information constants to answer messages and possibly database table fields
 */

public interface Constants {

    String REQUEST_START = "Для начала работы введите /start";
    String START_DESCRIPTION = "Привет! Я такой-то такой-то бот, созданный для предоставления " +
            "всей необходимой информации людям, желающим принять наших милых обитателей в любящую и счастливую семью.";
    String CHOOSE_OPTION = "Выберите необходимый пункт меню.";
    String GREETINGS = "Приветствуем Вас в нашем приюте!";
    String CONSULT_NEW_USER = "Узнать информацию о приюте";
    String ABOUT = "";
    String SCHEDULE = "";
    String ADDRESS = "";
    String PRECAUTIONS = "";
    String LEAVE_CONTACTS = "";
    String CALL_VOLUNTEER = "";
    String CONSULT_POTENTIAL_OWNER = "Как взять собаку из приюта";
    String DATING_RULES = "";
    String DOCS_LIST = "";
    String TRANSPORTATION = "";
    String PUPPY_IMPROVEMENT = "";
    String ADULT_IMPROVEMENT = "";
    String DISABILITIES_IMPROVEMENTS = "";
    String DOG_HANDLERS_TIPS = "";
    String PROVEN_DOG_HANDLERS = "";
    String REASONS_FOR_REFUSAL = "";
    String KEEPING_a_PET = "";
    String DAILY_REPORT = ""; // NOT String?
    String REQUEST_TEXT = "";
    String REQUEST_PHOTO = "";
    String REPORT_WARNING = "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно, как необходимо." +
            " Пожалуйста, подойди ответственнее к этому занятию. В противном случае волонтеры приюта будут обязаны" +
            " самолично проверять условия содержания собаки»";
    String CONGRATS = "";
    String EXTRA_TIME = "";
    String NOT_PASS = "";
}
