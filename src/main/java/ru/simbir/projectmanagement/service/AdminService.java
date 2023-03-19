package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.response.*;

/**
 * Сервис для администратора
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface AdminService {

    /**
     * Метод для получения списка всех задач с пагинацией.
     *
     * @param page - страница
     * @param size - количество элементов в странице
     * @return список задач с пагинацией
     */
    PageResponse<TaskResponse> getTasks(int page, int size);

    /**
     * Метод для получения списка всех релизов с пагинацией.
     *
     * @param page - страница
     * @param size - количество элементов в странице
     * @return список релизов с пагинацией
     */
    PageResponse<ReleaseResponse> getReleases(int page, int size);

    /**
     * Метод для получения списка всех проектов с пагинацией.
     *
     * @param page - страница
     * @param size - количество элементов в странице
     * @return список проектов с пагинацией
     */
    PageResponse<ProjectResponse> getProjects(int page, int size);

    /**
     * Метод для получения списка всех пользователей с пагинацией.
     *
     * @param page - страница
     * @param size - количество элементов в странице
     * @return список пользователей с пагинацией
     */
    PageResponse<UserResponse> getUsers(int page, int size);
}
