package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskExecutionException extends ResponseStatusException {

    public TaskExecutionException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public TaskExecutionException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }
}
