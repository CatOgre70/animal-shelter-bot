package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;

public enum Shelter {

    DOG_SHELTER("Приют для собак"), CAT_SHELTER("Приют для кошек"), COW_SHELTER("Приют для коров");

    public String shelterSpecialization;

    Shelter(String s) {
        this.shelterSpecialization = s;
    }

    private static Map<String, Shelter> SHELTER_BY_SPECIALIZATION = new HashMap<>();

    static {
        for (Shelter s : values()) {
            SHELTER_BY_SPECIALIZATION.put(s.shelterSpecialization, s);
        }
    }

    public Shelter getShelterBySpecialization(String specialization) {
        return SHELTER_BY_SPECIALIZATION.get(specialization);
    }

}
