package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ReleaseException extends ResponseStatusException {

    public ReleaseException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }
}
