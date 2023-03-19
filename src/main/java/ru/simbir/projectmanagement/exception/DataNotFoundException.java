package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;

public class DataNotFoundException extends GlobalException {
    public DataNotFoundException(String s) {
        super(HttpStatus.NOT_FOUND, s);
    }

    public DataNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
