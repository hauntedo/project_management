package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.UserUpdateRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;

import java.util.UUID;

/**
 * Данный сервис отвечает за операции с пользователем
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface UserService {


    /**
     * Метод для получения пользователя по id
     *
     * @param userId - id пользователя
     * @return пользователя, если он найден, пустого пользователя, если не найден
     */
    UserResponse getUserById(UUID userId);

    /**
     * Метод для обновления данных пользователя по id
     *
     * @param userId            - id пользователя
     * @param userUpdateRequest - новые данные пользователя
     * @return обновленного пользователя
     * @throws DataNotFoundException - если пользователь по id не найден
     */
    UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest) throws DataNotFoundException;

    /**
     * Метод для получения проектов, над которыми пользователь работает
     *
     * @param userId - id пользователя
     * @param page   - страница
     * @param size   - количество элементов в странице
     * @return список проектов, если они были найдены, с пагинацией
     */
    PageResponse<ProjectResponse> getProjectsByUserId(UUID userId, int page, int size);
}
