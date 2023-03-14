package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FailureLoginException extends ResponseStatusException {

    public FailureLoginException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public FailureLoginException(HttpStatus status) {
        super(status);
    }
}
