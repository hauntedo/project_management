package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OccupiedDataException extends ResponseStatusException {
    public OccupiedDataException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }


}
