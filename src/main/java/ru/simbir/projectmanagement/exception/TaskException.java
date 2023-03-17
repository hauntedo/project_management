package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskException extends ResponseStatusException {

    public TaskException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public TaskException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }
}
