package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * this class contains an enumeration of shelter animal types
 */
public enum Shelter {

    DOG_SHELTER("Dog shelter"), CAT_SHELTER("Cat shelter"), COW_SHELTER("Cow shelter");

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
