package pl.szleperm.messenger.web.rest.utils;

import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FieldErrorModel {
    private final String field;
    private final String message;

    public FieldErrorModel(FieldError error) {
        this.field = error.getField();
        this.message = Arrays.stream(error.getField().split("(?=[A-Z])"))
                .map(String::toLowerCase)
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "))
                + " " + error.getDefaultMessage();
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

}
