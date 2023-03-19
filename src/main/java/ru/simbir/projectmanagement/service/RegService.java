package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.exception.OccupiedDataException;

import java.util.UUID;

/**
 * Данный сервис отвечает за регистрацию пользователя
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface RegService {

    /**
     * Метод для регистрации пользователя
     *
     * @param registrationRequest - данные пользователя для регистрации
     * @return Id нового пользователя
     * @throws OccupiedDataException - если существует пользователь с введенными данными
     */
    UUID registerUser(RegistrationRequest registrationRequest) throws OccupiedDataException;
}
