package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.RegistrationRequest;

import java.util.UUID;

public interface RegService {
    UUID registerUser(RegistrationRequest registrationRequest);
}
