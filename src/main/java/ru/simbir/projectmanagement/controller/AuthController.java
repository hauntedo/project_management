package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.AuthApi;
import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;
import ru.simbir.projectmanagement.service.AuthenticationService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class AuthController implements AuthApi {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<TokenResponse> authenticate(AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @Override
    public ResponseEntity<UUID> register(RegistrationRequest registrationRequest) {
        return ResponseEntity.status(201).body(authenticationService.register(registrationRequest));
    }
}
