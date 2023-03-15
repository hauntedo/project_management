package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ReleaseVersionException extends ResponseStatusException {

    public ReleaseVersionException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }

    public ReleaseVersionException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
