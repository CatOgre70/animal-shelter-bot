package dev.pro.animalshelterbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class DailyReportNotFoundException extends RuntimeException{

    public DailyReportNotFoundException(String s) {
        super(s);
    }

}
