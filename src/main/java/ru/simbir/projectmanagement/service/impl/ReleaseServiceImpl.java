package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.ReleaseRequest;
import ru.simbir.projectmanagement.dto.response.ReleaseResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.ReleaseException;
import ru.simbir.projectmanagement.exception.ReleaseVersionException;
import ru.simbir.projectmanagement.model.Release;
import ru.simbir.projectmanagement.model.Task;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ReleaseRepository;
import ru.simbir.projectmanagement.repository.TaskRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.ReleaseService;
import ru.simbir.projectmanagement.utils.enums.TaskState;
import ru.simbir.projectmanagement.utils.mapper.ReleaseMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

    private static final Logger LOGGER = LogManager.getLogger(ReleaseServiceImpl.class);

    private final ReleaseRepository releaseRepository;
    private final ReleaseMapper releaseMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private static void validateRelease(ReleaseRequest releaseRequest, Task task) {
        //ПРОВЕРЯЕМ КОРРЕКТНОСТЬ РЕЛИЗОВ
        List<Release> releases = task.getReleases();
        if (!releases.isEmpty()) {
            for (Release release : releases) {
                checkReleaseEnd(release);
                checkReleaseVersion(releaseRequest, release);
            }
        }
    }

    private static void checkReleaseVersion(ReleaseRequest releaseRequest, Release release) {
        //ПРОВЕРЯЕМ ВАЛИДНОСТЬ ВЕРСИИ РЕЛИЗА, НАПРИМЕР ПОСЛЕ 1.0.2 НЕЛЬЗЯ ДОБАВИТЬ 1.0.1
        if (release.getVersion().compareTo(releaseRequest.getVersion()) >= 0) {
            LOGGER.warn("#checkReleaseVersion: release by id {} is not completed. {}", release.getId(),
                    ReleaseVersionException.class.getSimpleName());
            throw new ReleaseVersionException("Current version of the release is smaller than the existing ones");
        }
    }

    private static void checkReleaseEnd(Release release) {
        //ПРОВЕРЯЕМ, ИМЕЕТ ЛИ РЕЛИЗ ВРЕМЯ ЗАВЕРШЕНИЯ
        if (release.getEnd() != null) {
            LOGGER.warn("#checkReleaseEnd: release by id {} is not completed. {}", release.getId(),
                    ReleaseException.class.getSimpleName());
            throw new ReleaseException("Release is not completed: " + release.getId());
        }
    }

    private static void checkAccessTaskForUser(String username, Task task) {
        //ПРОВЕРЯЕМ, ЯВЛЯЕТСЯ ЛИ ПОЛЬЗОВАТЕЛЬ АВТОРОМ ИЛИ ИСПОЛНИТЕЛЕМ ЗАДАЧИ
        if (task.getDeveloper() == null || !task.getDeveloper().getEmail().equals(username) && !task.getAuthor().getEmail().equals(username)) {
            LOGGER.warn("#addRelease: no access to control release for current user by email {}. {}", username,
                    AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to control release for user by email " + username);
        }
    }

    private static void checkTaskStateInProgress(Task task) {
        //ПРОВЕРЯЕМ, НАХОДИТСЯ ЛИ ЗАДАЧА В СОСТОЯНИИ 'IN_PROGRESS'
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#checkTaskStateInProgress: task by id {} has not state 'IN_PROGRESS'. {}", task.getId(),
                    ReleaseException.class.getSimpleName());
            throw new ReleaseException("Release can be added if task status is IN_PROGRESS. Task id " + task.getId());
        }
    }

    private static void checkReleaseVersionToUpdate(UUID releaseId, ReleaseRequest releaseRequest, Release release) {
        //ПРОВЕРЯЕМ, БЫЛА ЛИ ПОПЫТКА ИЗМЕНЕНИИ ВЕРСИИ РЕЛИЗА ПРИ ОБНОВЛЕНИИ
        if (!release.getVersion().equals(releaseRequest.getVersion())) {
            LOGGER.warn("#checkReleaseVersionToUpdate: release by id {} version is not upgradable. {}", releaseId,
                    ReleaseException.class.getSimpleName());
            throw new ReleaseException("Release version no upgradable. Id " + releaseId);
        }
    }

    private static void checkAccessReleaseForUser(String username, Release release) {
        //ПРОВЕРЯЕМ, ЧТО ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ ЯВЛЯЕТСЯ ЛИБО ИСПОЛНИТЕЛЕМ, ЛИБО АВТОРОМ ЗАДАЧИ
        if (!release.getDeveloper().getEmail().equals(username) && !release.getTask().getAuthor().getEmail().equals(username)) {
            LOGGER.warn("#checkAccessReleaseForUser: no access to control release for current user by email {}. {}", username,
                    AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to control release for user by email " + username);
        }
    }

    @Transactional
    @Override
    public ReleaseResponse addRelease(ReleaseRequest releaseRequest, String username) {
        User developer = getUserByUsername(username);
        UUID taskId = releaseRequest.getTaskId();
        Task task = getTaskById(taskId);
        checkTaskStateInProgress(task);
        checkAccessTaskForUser(username, task);
        validateRelease(releaseRequest, task);
        Release newRelease = createRelease(releaseRequest, developer, task);
        newRelease = saveRelease(newRelease);
        return releaseMapper.toResponse(newRelease);
    }

    @Transactional(readOnly = true)
    @Override
    public ReleaseResponse getReleaseById(UUID releaseId) {
        Release release = getRelease(releaseId);
        return releaseMapper.toResponse(release);
    }

    @Transactional
    @Override
    public ReleaseResponse updateReleaseById(UUID releaseId, String username, ReleaseRequest releaseRequest) {
        Release release = getRelease(releaseId);
        checkAccessReleaseForUser(username, release);
        checkReleaseVersionToUpdate(releaseId, releaseRequest, release);
        checkReleaseEnd(release);
        release.setDescription(releaseRequest.getDescription());
        release = saveRelease(release);
        return releaseMapper.toResponse(release);
    }

    @Transactional
    @Override
    public ReleaseResponse closeRelease(UUID releaseId, String username) {
        Release release = getRelease(releaseId);
        checkAccessReleaseForUser(username, release);
        checkReleaseEnd(release);
        release.setEnd(Instant.now());
        release = saveRelease(release);
        return releaseMapper.toResponse(release);
    }

    private Release getRelease(UUID releaseId) {
        LOGGER.info("#getRelease: find release by id {}", releaseId);
        Optional<Release> optionalRelease = releaseRepository.findById(releaseId);
        if (!optionalRelease.isPresent()) {
            LOGGER.warn("#getRelease: release by id {} not found", releaseId);
            throw new DataNotFoundException("Not found release by id " + releaseId);
        }
        return optionalRelease.get();
    }

    private Release saveRelease(Release newRelease) {
        LOGGER.info("#saveRelease: try to save release");
        newRelease = releaseRepository.save(newRelease);
        LOGGER.info("#saveRelease: release by id {} saved", newRelease.getId());
        return newRelease;
    }

    private Release createRelease(ReleaseRequest releaseRequest, User developer, Task task) {
        Release newRelease = releaseMapper.toEntity(releaseRequest);
        newRelease.setStart(Instant.now());
        newRelease.setTask(task);
        newRelease.setDeveloper(developer);
        return newRelease;
    }

    private Task getTaskById(UUID taskId) {
        LOGGER.info("#getTaskById: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#getTaskById: task by id {} not found. {}", taskId,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        return optionalTask.get();
    }

    private User getUserByUsername(String username) {
        LOGGER.info("#getUserByUsername: find user by email {}", username);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#getUserByUsername: user by email {} not found", username);
            throw new DataNotFoundException("Not found user by email " + username);
        }
        return optionalUser.get();
    }
}
