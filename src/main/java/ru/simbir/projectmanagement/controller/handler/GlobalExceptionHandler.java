package ru.simbir.projectmanagement.controller.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.simbir.projectmanagement.dto.response.ExceptionResponse;
import ru.simbir.projectmanagement.dto.validation.ValidationResponse;
import ru.simbir.projectmanagement.exception.GlobalException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> onAllExceptions(Exception ex, WebRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .date(Instant.now().toString())
                        .message(ex.getMessage())
                        .exceptionName(ex.getClass().getSimpleName())
                        .description(request.getDescription(false))
                        .build());

    }

    @ExceptionHandler(GlobalException.class)
    public final ResponseEntity<ExceptionResponse> onGlobalException(GlobalException globalException, WebRequest request) {
        return ResponseEntity
                .status(globalException.getStatus())
                .body(
                        ExceptionResponse.builder()
                                .date(Instant.now().toString())
                                .message(globalException.getMessage())
                                .description(request.getDescription(false))
                                .exceptionName(globalException.getClass().getSimpleName())
                                .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> onAuthenticationExceptions(AuthenticationException ex, WebRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .date(Instant.now().toString())
                        .message(ex.getMessage())
                        .exceptionName(ex.getClass().getSimpleName())
                        .description(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(JWTCreationException.class)
    public final ResponseEntity<ExceptionResponse> onJwtCreationExceptions(JWTCreationException ex, WebRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .date(Instant.now().toString())
                        .message(ex.getMessage())
                        .exceptionName(ex.getClass().getSimpleName())
                        .description(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public final ResponseEntity<ExceptionResponse> onJwtVerificationExceptions(JWTVerificationException ex, WebRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .date(Instant.now().toString())
                        .message(ex.getMessage())
                        .exceptionName(ex.getClass().getSimpleName())
                        .description(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ExceptionResponse.builder()
                        .date(Instant.now().toString())
                        .message(ex.getMessage())
                        .exceptionName(ex.getClass().getSimpleName())
                        .description(request.getDescription(false))
                        .build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }
        ValidationResponse error = new ValidationResponse(HttpStatus.BAD_REQUEST, errors);
        return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    }
}
