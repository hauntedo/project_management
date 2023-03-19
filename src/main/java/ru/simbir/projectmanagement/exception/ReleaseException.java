package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;

public class ReleaseException extends GlobalException {

    public ReleaseException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }
}
