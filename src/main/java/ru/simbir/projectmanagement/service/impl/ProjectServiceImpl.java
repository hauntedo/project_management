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
import ru.simbir.projectmanagement.exception.OccupiedDataException;
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

    private static void checkAccessToOperateFromProject(String username, String projectOwnerEmail) {
        //ПРОВЕРЯЕМ ДОСТУП ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ НА УПРАВЛЕНИЕ ПРОЕКТОМ
        if (!projectOwnerEmail.equals(username)) {
            LOGGER.warn("#checkAccessToOperateFromProject: no access to operate from project for user by email {}", projectOwnerEmail);
            throw new AccessDeniedException("No access to control a project");
        }
    }

    private static void checkProjectStateBacklog(UUID projectId, Project project) {
        //ПРОВЕРЯЕМ, НАХОДИТСЯ ПРОЕКТ В СТАТУСЕ 'BACKLOG'
        if (project.getProjectState() != ProjectState.BACKLOG) {
            LOGGER.error("#checkProjectStateBacklog: project by id {} can only be run in BACKLOG state: {}", projectId,
                    EntityStateException.class.getSimpleName());
            throw new EntityStateException("Project can only be run in BACKLOG state");
        }
    }

    private static void checkTasksStateDone(Project project) {
        //ПРОВЕРЯЕМ, НАХОДЯТСЯ ЛИ ВСЕ ЗАДАЧИ В СТАТУСЕ 'DONE'
        for (Task t : project.getTasks()) {
            if (!t.getTaskState().equals(TaskState.DONE)) {
                LOGGER.error("#checkTasksStateDone: task state by id {} is not 'DONE': {}", t.getId(),
                        EntityStateException.class.getSimpleName());
                throw new EntityStateException("Project can only be closed if all tasks are in DONE status");
            }
        }
    }

    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, String username) {
        LOGGER.info("#createProject: find user by email {}", username);
        User user = getUser(username);
        String code = projectRequest.getCode();
        existProjectByCode(code);
        Project newProject = createProject(projectRequest, user);
        LOGGER.info("#createProject: try to save project {}", code);
        newProject = projectRepository.save(newProject);
        LOGGER.info("#createProject: project by id {} saved", newProject.getId());
        return projectMapper.toResponse(newProject);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username) {
        Project project = getProject(projectId);
        checkAccessToOperateFromProject(username, project.getOwner().getEmail());
        projectMapper.update(projectRequest, project);
        project = saveProject(project);
        return projectMapper.toResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse startProject(UUID projectId, String username) {
        Project project = getProject(projectId);
        checkAccessToOperateFromProject(username, project.getOwner().getEmail());
        checkProjectStateBacklog(projectId, project);
        project.setProjectState(ProjectState.IN_PROGRESS);
        project = saveProject(project);
        return projectMapper.toResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse endProject(UUID projectId, String username) {
        Project project = getProject(projectId);
        checkAccessToOperateFromProject(username, project.getOwner().getEmail());
        checkTasksStateDone(project);
        project.setProjectState(ProjectState.DONE);
        project = saveProject(project);
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponse getProjectById(UUID projectId, String username) {
        Project project = getProject(projectId);
        if (!checkProjectUsers(username, project)) {
            LOGGER.error("#getProjectById: no access for current user by email {}. {}", username,
                    AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access project for current user");
        }
        return projectMapper.toResponse(project);
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
        User user = getUser(username);
        Project project = getProjectByProjectCode(projectCode);
        //ДОБАВЛЯЕМ НОВОГО ПОЛЬЗОВАТЕЛЯ В СПИСОК УЧАСТНИКОВ
        project.getUsers().add(user);
        saveProject(project);
        return SuccessResponse.builder()
                .time(Instant.now())
                .message("Successfully joined")
                .build();
    }

    private Project getProjectByProjectCode(String projectCode) {
        Optional<Project> optionalProject = projectRepository.findByCode(projectCode);
        if (!optionalProject.isPresent()) {
            LOGGER.error("#getProjectByProjectCode: not found project by code {}", projectCode);
            throw new DataNotFoundException("Not found project by code " + projectCode);
        }
        return optionalProject.get();
    }

    private boolean checkProjectUsers(String username, Project project) {
        //ТУТ ПРОВЕРЯЕМ, ЧТО ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ ЯВЛЯЕТСЯ УЧАСТНИКОМ ПРОЕКТА
        for (User user : project.getUsers()) {
            if (user.getEmail().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private Project createProject(ProjectRequest projectRequest, User user) {
        Project newProject = projectMapper.toEntity(projectRequest);
        newProject.setProjectState(ProjectState.BACKLOG);
        newProject.setOwner(user);
        Set<User> set = new HashSet<>();
        set.add(user);
        newProject.setUsers(set);
        return newProject;
    }

    private void existProjectByCode(String code) {
        //ПРОВЕРЯЕМ, СУЩЕСТВУЕТ ЛИ ДАННЫЙ КОД ПРОЕКТА
        if (projectRepository.existsByCode(code)) {
            LOGGER.warn("#existProjectByCode: occupied project code {}. {}", code,
                    OccupiedDataException.class.getSimpleName());
            throw new OccupiedDataException("Project code is occupied");
        }
    }

    private User getUser(String username) {
        LOGGER.info("#getUser: find user by email {}", username);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#getUser: user by email {} not found", username);
            throw new DataNotFoundException("User by email {} " + username + " not found");
        }
        return optionalUser.get();
    }

    private Project saveProject(Project project) {
        LOGGER.info("#saveProject: try to save project by id {}", project.getId());
        project = projectRepository.save(project);
        LOGGER.info("#saveProject: save project by id {}", project.getId());
        return project;
    }

    private Project getProject(UUID projectId) {
        LOGGER.info("#getProject: find project by id {}", projectId);
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#getProject: project by id {} not found", projectId);
            throw new DataNotFoundException("Project by id {} " + projectId + " not found");
        }
        return optionalProject.get();
    }
}
