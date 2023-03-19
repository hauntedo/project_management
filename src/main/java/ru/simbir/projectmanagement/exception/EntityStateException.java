package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;

public class EntityStateException extends GlobalException {
    public EntityStateException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }

    public EntityStateException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
