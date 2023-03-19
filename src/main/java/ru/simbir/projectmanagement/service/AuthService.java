package ru.simbir.projectmanagement.service;

import org.springframework.web.server.ResponseStatusException;
import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;

/**
 * Данный сервис отвечает за авторизацию пользователя
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface AuthService {

    /**
     * Метод для авторизации пользователя
     *
     * @param authenticationRequest - данные пользователя для авторизации
     * @return JWT-токен
     * @throws ResponseStatusException - если запрос аутентификации отклонен в связи c недействительными данными
     */
    TokenResponse authenticate(AuthenticationRequest authenticationRequest) throws ResponseStatusException;


}
