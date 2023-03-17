package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.*;
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

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOGGER = LogManager.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;

    private static void checkAccessToOperate(String username, String projectOwnerEmail) {
        if (!projectOwnerEmail.equals(username)) {
            LOGGER.error("#checkAccessToOperate: {}", projectOwnerEmail);
            throw new AccessDeniedException("No access to control a project");
        }
    }

    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, String username) {
        LOGGER.info("#createProject: find user by email {}", username);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.error("#createProject: user by email {} not found", username);
            throw new DataNotFoundException("User by email {} " + username + " not found");
        }
        User user = optionalUser.get();
        Project newProject = projectMapper.toEntity(projectRequest);
        newProject.setProjectState(ProjectState.BACKLOG);
        newProject.setOwner(user);
        Set<User> set = new HashSet<>();
        set.add(user);
        newProject.setUsers(set);
        LOGGER.info("#createProject: try to save project {}", projectRequest.getCode());
        newProject = projectRepository.save(newProject);
        LOGGER.info("#createProject: project by id {} saved", newProject.getId());
        return projectMapper.toResponse(newProject);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username) {
        LOGGER.info("#updateProjectById: find project by id {}", projectId);
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#updateProjectById: project by id {} not found", projectId);
            throw new DataNotFoundException("Project by id {} " + projectId + " not found");
        }
        Project project = optionalProject.get();
        //проверка на доступ над проектом для пользователя
        checkAccessToOperate(username, project.getOwner().getEmail());
        projectMapper.update(projectRequest, project);
        LOGGER.info("#updateProjectById: try to save project by id {}", projectId);
        project = projectRepository.save(project);
        LOGGER.info("#updateProjectById: save project by id {}", projectId);
        return projectMapper.toResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse startProject(UUID projectId, String username) {
        LOGGER.info("#startProject: find project by id {}", projectId);
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#startProject: project by id {} not found", projectId);
            throw new DataNotFoundException("Project by id {} " + projectId + " not found");
        }
        Project project = optionalProject.get();
        //проверка на доступ над проектом для пользователя
        checkAccessToOperate(username, project.getOwner().getEmail());
        //проверка, что проект еще не запущен
        if (project.getProjectState() != ProjectState.BACKLOG) {
            LOGGER.error("#startProject: project by id {} can only be run in BACKLOG state: {}", projectId,
                    EntityStateException.class.getSimpleName());
            throw new EntityStateException("Project can only be run in BACKLOG state");
        }
        project.setProjectState(ProjectState.IN_PROGRESS);
        LOGGER.info("#startProject: try to save project by id {}", projectId);
        project = projectRepository.save(project);
        LOGGER.info("#startProject: save project by id {}", projectId);
        return projectMapper.toResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse endProject(UUID projectId, String username) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#endProject: project by id {} not found", projectId);
            throw new DataNotFoundException("Project by id {} " + projectId + " not found");
        }
        Project project = optionalProject.get();
        //проверка на доступ над проектом для пользователя
        checkAccessToOperate(username, project.getOwner().getEmail());
        //проверка, что все задачи завершены
        for (Task t : project.getTasks()) {
            if (!t.getTaskState().equals(TaskState.DONE)) {
                LOGGER.error("#endProject: task state by id {} is not 'DONE': {}", t.getId(),
                        EntityStateException.class.getSimpleName());
                throw new EntityStateException("Project can only be closed if all tasks are in DONE status");
            }
        }
        project.setProjectState(ProjectState.DONE);
        LOGGER.info("#endProject: try to save project by id {}", projectId);
        project = projectRepository.save(project);
        LOGGER.info("#endProject: save project by id {}", projectId);
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponse getProjectById(UUID projectId, String username) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#getProjectById: project by id {} not found", projectId);
            return ProjectResponse.builder().build();
        }
        Project project = optionalProject.get();
        //информацию о проекте может получить только участник
        for (User user : project.getUsers()) {
            if (user.getEmail().equals(username)) {
                return projectMapper.toResponse(project);
            }
        }
        LOGGER.error("#getProjectById: no access for current user by email {}. {}", username,
                AccessDeniedException.class.getSimpleName());
        throw new AccessDeniedException("No access project for current user");
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<TaskResponse> getTasksByProjectId(UUID projectId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getTasksByProjectId: find all tasks by project id {}", projectId);
        Page<Task> taskPage = taskRepository.findAllByProject_Id(projectId, pageRequest);
        return PageResponse.<TaskResponse>builder()
                .content(taskMapper.toList(taskPage.getContent()))
                .totalPages(taskPage.getTotalPages())
                .totalElements(taskPage.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserResponse> getUsersByProjectId(UUID projectId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getUsersByProjectId: find all users by project id {}", projectId);
        Page<User> userPage = userRepository.findAllByProjects_Id(projectId, pageRequest);
        return PageResponse.<UserResponse>builder()
                .content(userMapper.toList(userPage.getContent()))
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .build();
    }

    @Transactional
    @Override
    public SuccessResponse joinProjectByCode(String projectCode, String username) {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.error("#joinProjectByCode: user by email {} not found. {}", username, DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Not found user by email " + username);
        }
        Optional<Project> optionalProject = projectRepository.findByCode(projectCode);
        if (!optionalProject.isPresent()) {
            LOGGER.error("#joinProjectByCode: not found project by code {}", projectCode);
            throw new DataNotFoundException("Not found project by code " + projectCode);
        }
        User user = optionalUser.get();
        Project project = optionalProject.get();
        project.getUsers().add(user);
        LOGGER.info("#joinProjectByCode: try to save project by code {}", projectCode);
        projectRepository.save(project);
        LOGGER.info("#joinProjectByCode: save project by code {}", projectCode);
        return SuccessResponse.builder()
                .time(Instant.now())
                .message("Successfully joined")
                .build();
    }
}
