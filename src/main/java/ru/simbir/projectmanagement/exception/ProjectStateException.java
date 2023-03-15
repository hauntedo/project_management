package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProjectStateException extends ResponseStatusException {
    public ProjectStateException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }

    public ProjectStateException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
