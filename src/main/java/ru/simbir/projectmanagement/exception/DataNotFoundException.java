package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DataNotFoundException extends ResponseStatusException {
    public DataNotFoundException(String s) {
        super(HttpStatus.NOT_FOUND, s);
    }

    public DataNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
