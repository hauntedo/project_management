package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;

import java.util.UUID;

public interface AuthenticationService {

    TokenResponse authenticate(AuthenticationRequest authenticationRequest);

    UUID register(RegistrationRequest registrationRequest);

}
