package dev.pro.animalshelterbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AdoptedAnimalsNotFoundException extends RuntimeException{

    public AdoptedAnimalsNotFoundException(String str) {
        super(str);
    }

}
