package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

    private final ReleaseRepository releaseRepository;
    private final ReleaseMapper releaseMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private static void validateRelease(ReleaseRequest releaseRequest, Task task) {
        List<Release> releases = task.getReleases();
        if (!releases.isEmpty()) {
            for (Release release : releases) {
                if (release.getEnd() != null) {
                    throw new ReleaseException("Release is not completed: " + release.getId());
                }
                if (release.getVersion().compareTo(releaseRequest.getVersion()) >= 0) {
                    throw new ReleaseVersionException("Current version of the release is smaller than the existing ones");
                }
            }
        }
    }

    @Transactional
    @Override
    public ReleaseResponse addRelease(ReleaseRequest releaseRequest, String username) {
        User developer = userRepository.findByEmail(username).orElseThrow(DataNotFoundException::new);
        Task task = taskRepository.findById(releaseRequest.getTaskId()).orElseThrow(DataNotFoundException::new);
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            throw new ReleaseException("Release can only be added if task status is IN_PROGRESS");
        }
        if (task.getDeveloper().getEmail().equals(username) || task.getAuthor().getEmail().equals(username)) {
            validateRelease(releaseRequest, task);
            Release newRelease = releaseMapper.toEntity(releaseRequest);
            newRelease.setStart(Instant.now());
            newRelease.setTask(task);
            newRelease.setDeveloper(developer);
            return releaseMapper.toResponse(releaseRepository.save(newRelease));
        } else {
            throw new AccessDeniedException("No access to control release");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReleaseResponse getReleaseById(UUID releaseId) {
        return releaseMapper.toResponse(releaseRepository.findById(releaseId).orElseThrow(DataNotFoundException::new));
    }

    @Transactional
    @Override
    public ReleaseResponse updateReleaseById(UUID releaseId, String username, ReleaseRequest releaseRequest) {
        Release release = releaseRepository.findById(releaseId).orElseThrow(DataNotFoundException::new);
        if (release.getDeveloper().getEmail().equals(username) || release.getTask().getAuthor().getEmail().equals(username)) {
            if (!release.getVersion().equals(releaseRequest.getVersion())) {
                throw new ReleaseException("Release version no upgradable");
            }
            if (release.getEnd() != null) {
                throw new ReleaseException("Release completed");
            }
            release.setDescription(releaseRequest.getDescription());
            return releaseMapper.toResponse(releaseRepository.save(release));
        } else {
            throw new AccessDeniedException("No access to control this release");
        }
    }
}
