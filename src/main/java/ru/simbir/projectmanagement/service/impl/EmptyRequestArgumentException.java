package ru.simbir.projectmanagement.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmptyRequestArgumentException extends ResponseStatusException {


    public EmptyRequestArgumentException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }
}
