package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmptyRequestArgumentException extends ResponseStatusException {


    public EmptyRequestArgumentException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }
}
