package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityStateException extends ResponseStatusException {
    public EntityStateException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }

    public EntityStateException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
