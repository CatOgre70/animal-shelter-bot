package dev.pro.animalshelterbot.menu;

import java.util.HashMap;
import java.util.Map;

public enum Buttons {

    // Start menu "Chose shelter"
    DOG_SHELTER("Приют для собак", "DogShelter", ButtonType.CALLBACK, null),
    CAT_SHELTER("Приют для кошек", "CatShelter", ButtonType.CALLBACK, null),

    // Default root menu
    SHELTER_INFO("Информация о приюте", "ShelterInfo", ButtonType.CALLBACK, null),
    ANIMAL_INFO("Как взять животное", "AnimalInfo", ButtonType.CALLBACK, null),
    DAILY_REPORT("Отправить отчет о питомце", "DailyReport", ButtonType.CALLBACK, null),

    // Service buttons
    HELP("Справка - как работать с ботом", "help", ButtonType.CALLBACK, null),
    BACK("Обратно, к предыдущему меню", "backToPreviousMenu", ButtonType.CALLBACK, null),
    SEND_PHONE_AND_ADDRESS("Оставить контактные данные для связи", "userContact", ButtonType.CALLBACK, null),
    CALL_VOLUNTEER("Позвать волонтера", "CallVolunteers", ButtonType.CALLBACK, null),


    // Information about dog shelter
    DOG_SHELTER_INFO("Общая информация о приюте", "DogShelterInfo", ButtonType.CALLBACK, null),
    DOG_SHELTER_ADDRESS("Расписание работы, адрес, схема проезда", "DogShelterAddr", ButtonType.URL,
            "https://www.instagram.com/asenka_astana/"),
    DOG_SHELTER_SECURITY("Как заказать пропуск на машину", "DogShelterSec", ButtonType.URL,
            "https://www.instagram.com/asenka_astana/"),
    DOG_SHELTER_SAFETY_TIPS("О технике безопасности на территории приюта", "DogShelterSafe", ButtonType.URL,
            "https://www.instagram.com/asenka_astana/"),


    // Information about cat shelter
    CAT_SHELTER_INFO("Общая информация о приюте", "CatShelterInfo", ButtonType.CALLBACK, null),
    CAT_SHELTER_ADDRESS("Расписание работы, адрес, схема проезда", "CatShelterAddr", ButtonType.URL,
            "https://www.instagram.com/asenka_astana/"),
    CAT_SHELTER_SECURITY("Как заказать пропуск на машину", "CatShelterSec", ButtonType.URL,
            "https://www.instagram.com/asenka_astana/"),
    CAT_SHELTER_SAFETY_TIPS("О технике безопасности на территории приюта", "CatShelterSafe", ButtonType.URL,
            "https://www.instagram.com/asenka_astana/"),

    // How to adopt dog
    DOG_ANIMAL_WELCOME("Как знакомиться с животным в приюте", "DogAnimalWelcome", ButtonType.CALLBACK, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_DOCS("Документы, которые нужно оформить", "DogAnimalDocs", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_TRANSPORT("Рекомендации по транспортировке", "DogAnimalTransport", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_HOME1("Рекомендации по обустройству дома для щенка", "DogAnimalHome1", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_HOME2("Рекомендации по обустройству дома для взрослой собаки", "DogAnimalHome2", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_HOME3("Рекомендации по обустройству дома для собаки c ограниченными возможностями", "DogAnimalHome3", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_FIRST("Советы кинолога по первичному общению", "DogAnimalFirst", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_PROF("Проверенные кинологи", "DogAnimalProf", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    DOG_ANIMAL_REJECT("Почему могут отказать", "DogAnimalReject", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),


    // How to adopt cat
    CAT_ANIMAL_WELCOME("Как знакомиться с животным в приюте", "CatAnimalWelcome", ButtonType.CALLBACK, null),
    CAT_ANIMAL_DOCS("Документы, которые нужно оформить", "CatAnimalDocs", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    CAT_ANIMAL_TRANSPORT("Рекомендации по транспортировке", "CatAnimalTransport", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    CAT_ANIMAL_HOME1("Рекомендации по обустройству дома для котенка", "CatAnimalHome1", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    CAT_ANIMAL_HOME2("Рекомендации по обустройству дома для взрослой кошки", "CatAnimalHome2", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    CAT_ANIMAL_HOME3("Рекомендации по обустройству дома для кошки c ограниченными возможностями", "CatAnimalHome3", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),
    CAT_ANIMAL_REJECT("Почему могут отказать", "CatAnimalReject", ButtonType.URL, "https://www.instagram.com/asenka_astana/"),

    // Send daily report
    DR_GENERAL_WELL_BEING("Отправить описание общего самочувствия питомца", "DRGeneralWellBeing", ButtonType.CALLBACK, null),
    DR_DIET("Отправить описание питания питомца", "DRDiet", ButtonType.CALLBACK, null),
    DR_CHANGE_IN_BEHAVIOR("Отправить описание изменений в привычках", "DRChangeInBehavior", ButtonType.CALLBACK, null),
    DR_PHOTO("Отправить фото питомца", "DRPhoto", ButtonType.CALLBACK, null);

    public final String bText, bCallBack, url;
    public final ButtonType bType;

    Buttons(String bText, String bCallBack, ButtonType bType, String url) {
        this.bText = bText;
        this.bCallBack = bCallBack;
        this.bType = bType;
        this.url = url;
    }

    private static final Map<String, Buttons> BUTTON_BY_CALLBACK = new HashMap<>();

    static {
        for (Buttons b: values()) {
            BUTTON_BY_CALLBACK.put(b.bCallBack, b);
        }
    }

    public static Buttons getButtonByCallback(String bCallBack) {
        return BUTTON_BY_CALLBACK.get(bCallBack);
    }

}
