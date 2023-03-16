package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.EntityStateException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.Task;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.TaskRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.ProjectService;
import ru.simbir.projectmanagement.utils.enums.ProjectState;
import ru.simbir.projectmanagement.utils.enums.TaskState;
import ru.simbir.projectmanagement.utils.mapper.ProjectMapper;
import ru.simbir.projectmanagement.utils.mapper.TaskMapper;
import ru.simbir.projectmanagement.utils.mapper.UserMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;

    private static void checkAccessToOperate(String username, String projectOwnerEmail) {
        if (!projectOwnerEmail.equals(username)) {
            throw new AccessDeniedException("No access to control a project");
        }
    }

    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, String username) {
        User user = userRepository.findByEmail(username).orElseThrow(DataNotFoundException::new);
        Project newProject = projectMapper.toEntity(projectRequest);
        newProject.setProjectState(ProjectState.BACKLOG);
        newProject.setOwner(user);
        Set<User> set = new HashSet<>();
        set.add(user);
        newProject.setUsers(set);
        return projectMapper.toResponse(projectRepository.save(newProject));
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(DataNotFoundException::new);
        checkAccessToOperate(username, project.getOwner().getEmail());
        projectMapper.update(projectRequest, project);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    @Override
    public ProjectResponse startProject(UUID projectId, String username) {
        Project project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);
        checkAccessToOperate(username, project.getOwner().getEmail());
        if (project.getProjectState() != ProjectState.BACKLOG) {
            throw new EntityStateException("Project can only be run in backlog state");
        }
        project.setProjectState(ProjectState.IN_PROGRESS);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    @Override
    public ProjectResponse endProject(UUID projectId, String username) {
        Project project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);
        checkAccessToOperate(username, project.getOwner().getEmail());
        for (Task t : project.getTasks()) {
            if (!t.getTaskState().equals(TaskState.DONE)) {
                throw new EntityStateException("Project can only be closed if all tasks are in DONE status");
            }
        }
        project.setProjectState(ProjectState.DONE);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProjectResponse> getProjects(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findAll(pageRequest);
        return PageResponse.<ProjectResponse>builder()
                .content(projectMapper.toList(projectPage.getContent()))
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponse getProjectById(UUID projectId, String username) {
        Project project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);
        for (User user : project.getUsers()) {
            if (user.getEmail().equals(username)) {
                return projectMapper.toResponse(project);
            }
        }
        throw new AccessDeniedException("No access project for current user");
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<TaskResponse> getTasksByProjectId(UUID projectId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findAllByProject_Id(projectId, pageRequest);
        return PageResponse.<TaskResponse>builder()
                .content(taskMapper.toList(taskPage.getContent()))
                .totalPages(taskPage.getTotalPages())
                .totalElements(taskPage.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<UserResponse> getUsersByProjectId(UUID projectId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllByProjects_Id(projectId, pageRequest);
        return PageResponse.<UserResponse>builder()
                .content(userMapper.toList(userPage.getContent()))
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .build();
    }
}
