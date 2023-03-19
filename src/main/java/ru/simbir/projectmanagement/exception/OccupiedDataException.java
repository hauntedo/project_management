package ru.simbir.projectmanagement.exception;

import org.springframework.http.HttpStatus;

public class OccupiedDataException extends GlobalException {
    public OccupiedDataException(String s) {
        super(HttpStatus.BAD_REQUEST, s);
    }


}
