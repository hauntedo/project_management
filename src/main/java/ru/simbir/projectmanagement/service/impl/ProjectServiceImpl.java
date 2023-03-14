package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.ProjectService;
import ru.simbir.projectmanagement.utils.enums.ProjectState;
import ru.simbir.projectmanagement.utils.mapper.ProjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, String username) {
        User user = userRepository.findByEmail(username).orElseThrow(DataNotFoundException::new);
        Project newProject = projectMapper.toEntity(projectRequest);
        newProject.setProjectState(ProjectState.BACKLOG);
        newProject.setOwner(user);
        return projectMapper.toResponse(projectRepository.save(newProject));
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(DataNotFoundException::new);
        if (!project.getOwner().getEmail().equals(username)) {
            throw new AccessDeniedException("No access to control a project");
        }
        projectMapper.update(projectRequest, project);
        return projectMapper.toResponse(projectRepository.save(project));
    }
}
