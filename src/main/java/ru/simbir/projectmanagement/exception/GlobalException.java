package ru.simbir.projectmanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class GlobalException extends ResponseStatusException {

    public GlobalException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    public GlobalException(HttpStatus httpStatus) {
        super(httpStatus);
    }
}
