package ru.simbir.projectmanagement.service;

import org.springframework.security.access.AccessDeniedException;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.SuccessResponse;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.TaskException;

import java.util.UUID;

/**
 * Сервис для управления задачами
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface TaskService {

    /**
     * Метод для создания задачи с параметрами. Задачу могут создавать только создатели проекта. Сначала ищет проект по id,
     * проверяет, является ли текущий пользователь создателем проекта, затем создает задачу.
     *
     * @param taskRequest - данные задачи, содержит в себе id проекта
     * @param username    - email текущего пользователя, полученного из user details
     * @return задачу
     * @throws DataNotFoundException - если проект по id не найден
     * @throws AccessDeniedException - если текущий пользователь не является создателем проекта
     */
    TaskResponse addTask(TaskRequest taskRequest, String username)
            throws DataNotFoundException, AccessDeniedException;

    /**
     * Метод для обновления задачи с параметрами. Задачу могут обновлять только авторы задачи. Сначала ищет задачу по id,
     * проверяет, является ли текущий пользователь автором задачи, затем обновляет задачу.
     *
     * @param taskRequest - данные задачи
     * @param username    - email текущего пользователя, полученного из user details
     * @param taskId      - id задачи
     * @return обновленную задачу
     * @throws DataNotFoundException - если задача по id не найдена
     * @throws AccessDeniedException - если текущий пользователь не является автором задачи
     */
    TaskResponse updateTask(TaskRequest taskRequest, UUID taskId, String username)
            throws DataNotFoundException, AccessDeniedException;

    /**
     * Метод для получения задачи по id.
     *
     * @param taskId - id задачи
     * @return задачу, если она найдена, пустую задачу, если не найдена
     */
    TaskResponse getTaskById(UUID taskId);

    /**
     * Метод для удаления задачи. Задачу могут удалять только авторы задачи. Сначала ищет задачу по id,
     * проверяет, является ли текущий пользователь автором задачи, затем удаляет задачу.
     *
     * @param username - email текущего пользователя, полученного из user details
     * @param taskId   - id задачи
     * @return ответ о том, что задача удалена успешно
     * @throws DataNotFoundException - если задача по id не найдена
     * @throws AccessDeniedException - если текущий пользователь не является автором задачи
     */
    SuccessResponse deleteTaskById(UUID taskId, String username)
            throws DataNotFoundException, AccessDeniedException;

    /**
     * Метод для перевода задачи в состояние 'BACKLOG'. В состояние 'BACKLOG' могут переводить либо автор, либо исполнитель
     * задачи. Также важно, чтобы задача была в состоянии 'IN_PROGRESS'.
     * Сначала ищет задачу по id, проверяет, что текущий пользователь является либо автором, либо исполнителем,
     * затем переводит задачу в соответствующее состояние
     *
     * @param username - email текущего пользователя, полученного из user details
     * @param taskId   - id задачи
     * @return задачу в состоянии 'BACKLOG'
     * @throws DataNotFoundException - если задача по id не найдена
     * @throws AccessDeniedException - если текущий пользователь не является автором либо исполнителем задачи
     * @throws TaskException         - если задача не находится в состоянии 'IN_PROGRESS'
     */
    TaskResponse updateTaskToBacklog(UUID taskId, String username)
            throws DataNotFoundException, AccessDeniedException, TaskException;

    /**
     * Метод для перевода задачи в состояние 'IN_PROGRESS'. В состояние 'IN_PROGRESS' могут переводить только участники проекта.
     * Сначала ищет задачу по id и пользователя по email, проверяет, запущен ли проект(состояние 'IN_PROGRESS'), запущена ли задача и
     * является ли пользователь участником проекта, и только потом переводит задачу в соответствующее состояние
     *
     * @param username - email текущего пользователя, полученного из user details
     * @param taskId   - id задачи
     * @return задачу в состоянии 'IN_PROGRESS'
     * @throws DataNotFoundException - если задача по id или пользователь по email не найдены
     * @throws AccessDeniedException - если текущий пользователь не является участником проекта
     * @throws TaskException         - если проект не находится в состоянии 'IN_PROGRESS' или задача уже запущена, находится в
     *                               состоянии 'IN_PROGRESS' или есть исполнитель задачи
     */
    TaskResponse updateTaskToInProgress(UUID taskId, String username);

    /**
     * Метод для перевода задачи в состояние 'DONE'. В состояние 'IN_PROGRESS' могут переводить автор или исполнитель задачи.
     * Сначала ищет задачу по id и пользователя по email, проверяет, закрыты ли все релизы задачи,
     * потом переводит задачу в соответствующее состояние
     *
     * @param username - email текущего пользователя, полученного из user details
     * @param taskId   - id задачи
     * @return задачу в состоянии 'IN_PROGRESS'
     * @throws DataNotFoundException - если задача по id не найдена
     * @throws AccessDeniedException - если текущий пользователь не является автором или исполнителем задачи
     * @throws TaskException         - если хоть один релиз не закрыт
     */
    TaskResponse updateTaskToDone(UUID taskId, String username);
}
