package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;

public class TaskException extends GlobalException {

    public TaskException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public TaskException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }
}
