package dev.pro.animalshelterbot.constants;

import java.util.HashMap;
import java.util.Map;

public enum AnimalKind {

    CAT("Cat"), DOG("Dog"), COW("Cow");

    public final String kind;

    AnimalKind(String s) {
        this.kind = s;
    }

    private static final Map<String, AnimalKind> ANIMAL_KIND_BY_KIND = new HashMap<>();

    static {
        for(AnimalKind a : values()) {
            ANIMAL_KIND_BY_KIND.put(a.kind, a);
        }
    }

    static public AnimalKind getAnimalKindByKind(String kind) {
        return ANIMAL_KIND_BY_KIND.get(kind);
    }

}
