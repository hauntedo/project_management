package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.RegApi;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.service.RegService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class RegController implements RegApi {

    private final RegService regService;

    @Override
    public ResponseEntity<UUID> register(RegistrationRequest registrationRequest) {
        return ResponseEntity.status(201).body(regService.registerUser(registrationRequest));
    }
}
