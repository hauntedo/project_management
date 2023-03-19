package ru.simbir.projectmanagement.service;

import org.springframework.security.access.AccessDeniedException;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.*;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.EntityStateException;

import java.util.UUID;

/**
 * Сервия для управления проектами
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface ProjectService {

    /**
     * Метод для создания проекта. Привязывает к нему пользователя, который создал этот проект(owner)
     *
     * @param projectRequest - данные нового проекта
     * @param username       - email текущего пользователя, полученного из user details
     * @return проект
     * @throws DataNotFoundException - если пользователь c данным email не существует
     */
    ProjectResponse createProject(ProjectRequest projectRequest, String username) throws DataNotFoundException;


    /**
     * Метод для обновления проекта по параметрам. Проект может обновлять только его создатель(owner).
     *
     * @param username       - email текущего пользователя, полученного из user details
     * @param projectId      - id проекта, который подлежит обновлению
     * @param projectRequest - обновленные данные проекта
     * @return обновленный проект
     * @throws DataNotFoundException - если проект по id не найден
     * @throws AccessDeniedException - если текущий пользователь не является создателем проекта
     */
    ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username)
            throws DataNotFoundException, AccessDeniedException;

    /**
     * Метод для запуска проекта(перевод в состояние IN_PROGRESS). Проект может запускать только его создатель(owner).
     *
     * @param username  - email текущего пользователя, полученного из user details
     * @param projectId - id проекта, который подлежит обновлению
     * @return запущенный проект(состояние IN_PROGRESS)
     * @throws DataNotFoundException - если проект по id не найден
     * @throws AccessDeniedException - если текущий пользователь не является создателем проекта
     * @throws EntityStateException  - если проект уже запущен или завершен(находится в состоянии IN_PROGRESS, DONE)
     */
    ProjectResponse startProject(UUID projectId, String username)
            throws DataNotFoundException, AccessDeniedException, EntityStateException;

    /**
     * Метод для завершения проекта(перевод в состояние DONE). Проект может завершить только его создатель(owner).
     *
     * @param username  - email текущего пользователя, полученного из user details
     * @param projectId - id проекта, который подлежит обновлению
     * @return завершенный проект(состояние DONE)
     * @throws DataNotFoundException - если проект по id не найден
     * @throws AccessDeniedException - если текущий пользователь не является создателем проекта
     * @throws EntityStateException  - если хоть одна задача проекта не завершена, не находится в состоянии 'DONE'
     */
    ProjectResponse endProject(UUID projectId, String username);


    /**
     * Метод для получения проекта по id. Проверяет, является ли пользователем создателем проекта(owner) или же
     * участвует в разработке проекта(users).
     *
     * @param username  - email текущего пользователя, полученного из user details
     * @param projectId - id проекта, который подлежит обновлению
     * @return проект, если он найден, пустой проект, если не найден
     * @throws AccessDeniedException - если текущий пользователь не является участником проекта
     */
    ProjectResponse getProjectById(UUID projectId, String username);


    /**
     * Метод для получения всех задач, которые принадлежат проекту, с пагинацией.
     *
     * @param page      - страница
     * @param size      - количество элементов в странице
     * @param projectId - id проекта
     * @return список задач с пагинацией
     */
    PageResponse<TaskResponse> getTasksByProjectId(UUID projectId, int page, int size);

    /**
     * Метод для получения всех пользователей, которые являются участниками проекта, с пагинацией.
     *
     * @param page      - страница
     * @param size      - количество элементов в странице
     * @param projectId - id проекта
     * @return список пользователей с пагинацией
     */
    PageResponse<UserResponse> getUsersByProjectId(UUID projectId, int page, int size);

    /**
     * Метод для присоединения к проекту по коду, который создал создатель проекта.
     *
     * @param username    - email текущего пользователя
     * @param projectCode - код проекта
     * @return ответ о том, что операция прошла успешно
     * @throws DataNotFoundException - если проект по коду или пользователь по email не найдены
     */
    SuccessResponse joinProjectByCode(String projectCode, String username) throws DataNotFoundException;
}
