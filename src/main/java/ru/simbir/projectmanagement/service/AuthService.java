package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;

public interface AuthService {

    TokenResponse authenticate(AuthenticationRequest authenticationRequest);


}
