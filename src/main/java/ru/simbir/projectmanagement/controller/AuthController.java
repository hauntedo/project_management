package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.AuthApi;
import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;
import ru.simbir.projectmanagement.service.AuthService;

@RequiredArgsConstructor
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<TokenResponse> authenticate(AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authService.authenticate(authenticationRequest));
    }
}
