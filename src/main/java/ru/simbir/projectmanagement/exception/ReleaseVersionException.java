package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;

public class ReleaseVersionException extends GlobalException {

    public ReleaseVersionException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }

    public ReleaseVersionException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
