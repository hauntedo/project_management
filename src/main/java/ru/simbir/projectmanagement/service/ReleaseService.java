package ru.simbir.projectmanagement.service;

import org.springframework.security.access.AccessDeniedException;
import ru.simbir.projectmanagement.dto.request.ReleaseRequest;
import ru.simbir.projectmanagement.dto.response.ReleaseResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.ReleaseException;
import ru.simbir.projectmanagement.exception.ReleaseVersionException;

import java.util.UUID;

/**
 * Сервис для управления релизами задач
 *
 * @author hauntedo
 * @since 17.03.23
 */
public interface ReleaseService {

    /**
     * Метод добавления релиза для задачи. Релиз может добавить только тот пользователь, который имеет доступ к проекту(owner)
     * или работает над задачей в текущий момент(developer). Также важно, чтобы задача была запущенной, в состоянии IN_PROGRESS
     *
     * @param releaseRequest - данные нового релиза, содержит в себе id задачи, к которой привязывается релиз
     * @param username       - email пользователя
     * @return новый релиз
     * @throws ReleaseException        - если не все релизы закрыты(имеют время завершения) и если задача, к которой привязан релиз, не находится
     *                                 в стадии разработки(IN_PROGRESS)
     * @throws DataNotFoundException   - если пользователь по email не найден и если задача по id не найдена
     * @throws ReleaseVersionException - если версия нового релиза меньше существующих версий других релизов
     * @throws AccessDeniedException   - если текущий пользователь не имеет доступа к задаче(no author or no developer)
     */
    ReleaseResponse addRelease(ReleaseRequest releaseRequest, String username)
            throws ReleaseException, ReleaseVersionException, DataNotFoundException, AccessDeniedException;

    /**
     * Метод для получения релиза по id
     *
     * @param releaseId - id релиза
     * @return релиз
     * @throws DataNotFoundException - если релиз не найден
     */
    ReleaseResponse getReleaseById(UUID releaseId) throws DataNotFoundException;

    /**
     * Метод обновления релиза. Обновлять можно только описание релиза
     *
     * @param releaseRequest - данные для обновления релиза
     * @param username       - email текущего пользователя
     * @return обновленный релиз
     * @throws ReleaseException      - если была попытка обновления версии релиза или если релиз уже закрыт(имеет время завершения)
     * @throws DataNotFoundException - если релиз по id не найден
     * @throws AccessDeniedException - если текущий пользователь не имеет доступа к задаче(no author or no developer)
     */
    ReleaseResponse updateReleaseById(UUID releaseId, String username, ReleaseRequest releaseRequest)
            throws ReleaseException, DataNotFoundException, AccessDeniedException;

    /**
     * Метод для закрытия релиза. Устанавливает время завершения, если релиз еще не был закрыт. Релиз может закрыть
     * автор или исполнитель задачи
     *
     * @param releaseId - id релиза
     * @param username  - email текущего пользователя
     * @return обновленный релиз
     * @throws ReleaseException      - если релиз уже закрыт(имеет время завершения)
     * @throws DataNotFoundException - если релиз по id не найден
     * @throws AccessDeniedException - если текущий пользователь не имеет доступа к задаче(no author or no developer)
     */
    ReleaseResponse closeRelease(UUID releaseId, String username)
            throws ReleaseException, DataNotFoundException, AccessDeniedException;
}
