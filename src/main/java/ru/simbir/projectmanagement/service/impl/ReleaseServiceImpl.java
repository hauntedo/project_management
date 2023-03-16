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
        List<Release> releases = task.getReleases();
        if (!releases.isEmpty()) {
            for (Release release : releases) {
                if (release.getEnd() != null) {
                    LOGGER.warn("#validateRelease: release by id {} is not completed. {}", release.getId(),
                            ReleaseException.class.getSimpleName());
                    throw new ReleaseException("Release is not completed: " + release.getId());
                }
                if (release.getVersion().compareTo(releaseRequest.getVersion()) >= 0) {
                    LOGGER.warn("#validateRelease: release by id {} is not completed. {}", release.getId(),
                            ReleaseException.class.getSimpleName());
                    throw new ReleaseVersionException("Current version of the release is smaller than the existing ones");
                }
            }
        }
    }

    @Transactional
    @Override
    public ReleaseResponse addRelease(ReleaseRequest releaseRequest, String username) {
        LOGGER.info("#addRelease: find user by email {}", username);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#addRelease: user by email {} not found", username);
            throw new DataNotFoundException("Not found user by email " + username);
        }
        UUID taskId = releaseRequest.getTaskId();
        User developer = optionalUser.get();
        LOGGER.info("#addRelease: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#addRelease: task by id {} not found. {}",taskId,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#addRelease: task by id {} has not state 'IN_PROGRESS'. {}", taskId,
                    ReleaseException.class.getSimpleName());
            throw new ReleaseException("Release can only be added if task status is IN_PROGRESS. Task id " + taskId);
        }
        if (task.getDeveloper().getEmail().equals(username) || task.getAuthor().getEmail().equals(username)) {
            validateRelease(releaseRequest, task);
            Release newRelease = releaseMapper.toEntity(releaseRequest);
            newRelease.setStart(Instant.now());
            newRelease.setTask(task);
            newRelease.setDeveloper(developer);
            LOGGER.info("#addRelease: try to save release for task by id {}", taskId);
            newRelease = releaseRepository.save(newRelease);
            LOGGER.info("#addRelease: release by id {} for task by id {} saved", newRelease.getId(), taskId);
            return releaseMapper.toResponse(newRelease);
        } else {
            LOGGER.warn("#addRelease: no access to control release for current user by email {}. {}", username,
                    AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to control release for user by email " + username);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReleaseResponse getReleaseById(UUID releaseId) {
        LOGGER.info("#getReleaseById: find release by id {}", releaseId);
        Optional<Release> optionalRelease = releaseRepository.findById(releaseId);
        if (!optionalRelease.isPresent()) {
            LOGGER.warn("#getReleaseById: release by id {} not found", releaseId);
            return ReleaseResponse.builder().build();
        }
        return releaseMapper.toResponse(optionalRelease.get());
    }

    @Transactional
    @Override
    public ReleaseResponse updateReleaseById(UUID releaseId, String username, ReleaseRequest releaseRequest) {
        LOGGER.info("#updateReleaseById: find release by id {}", releaseId);
        Optional<Release> optionalRelease = releaseRepository.findById(releaseId);
        if (!optionalRelease.isPresent()) {
            LOGGER.warn("#updateReleaseById: release by id {} not found", releaseId);
            throw new DataNotFoundException("Not found release by id " + releaseId);
        }
        Release release = optionalRelease.get();
        if (release.getDeveloper().getEmail().equals(username) || release.getTask().getAuthor().getEmail().equals(username)) {
            if (!release.getVersion().equals(releaseRequest.getVersion())) {
                LOGGER.warn("#updateReleaseById: release by id {} version is not upgradable. {}", releaseId,
                        ReleaseException.class.getSimpleName());
                throw new ReleaseException("Release version no upgradable. Id " + releaseId);
            }
            if (release.getEnd() != null) {
                LOGGER.warn("#updateReleaseById: release by id {} completed. {}", releaseId,
                        ReleaseException.class.getSimpleName());
                throw new ReleaseException("Completed release by id " + releaseId);
            }
            release.setDescription(releaseRequest.getDescription());
            LOGGER.info("#updateReleaseById: try to save release by id {}", releaseId);
            release = releaseRepository.save(release);
            LOGGER.info("#updateReleaseById: release by id {} saved", releaseId);
            return releaseMapper.toResponse(release);
        } else {
            LOGGER.warn("#updateReleaseById: no access to control release for current user by email {}. {}", username,
                AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to control release for user by email " + username);
        }
    }

    @Transactional
    @Override
    public ReleaseResponse closeRelease(UUID releaseId, String username) {
        LOGGER.info("#closeRelease: find release by id {}", releaseId);
        Optional<Release> optionalRelease = releaseRepository.findById(releaseId);
        if (!optionalRelease.isPresent()) {
            LOGGER.warn("#closeRelease: release by id {} not found. {}", releaseId,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Not found release by id " + releaseId);
        }
        Release release = optionalRelease.get();
        if (release.getDeveloper().getEmail().equals(username) || release.getTask().getAuthor().getEmail().equals(username)) {
            if (release.getEnd() != null) {
                LOGGER.warn("#closeRelease: release by id {} already completed. {}", releaseId,
                        ReleaseException.class.getSimpleName());
                throw new ReleaseException("Release already completed");
            }
            release.setEnd(Instant.now());
            LOGGER.info("#closeRelease: try to save release by id {}", releaseId);
            release = releaseRepository.save(release);
            LOGGER.info("#closeRelease: release by id {} saved", releaseId);
            return releaseMapper.toResponse(release);
        } else {
            LOGGER.warn("#closeRelease: no access to control release for current user by email {}. {}", username,
                AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to control release for user by email " + username);
        }
    }
}
