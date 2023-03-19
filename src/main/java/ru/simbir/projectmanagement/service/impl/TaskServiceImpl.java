package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.SuccessResponse;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.TaskException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.Release;
import ru.simbir.projectmanagement.model.Task;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.TaskRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.TaskService;
import ru.simbir.projectmanagement.utils.enums.ProjectState;
import ru.simbir.projectmanagement.utils.enums.TaskState;
import ru.simbir.projectmanagement.utils.mapper.TaskMapper;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {


    private static final Logger LOGGER = LogManager.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    private static void checkAccessToOperateTaskForUser(String username, Task task) {
        LOGGER.info("#checkAccessToUpdateTaskForUser: check access task by id {} for user by email {}", task.getId(), username);
        //ПРОВЕРЯЕМ, ИМЕЕТ ЛИ ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ ДОСТУП К УПРАВЛЕНИЮ ЗАДАЧЕЙ
        if (!task.getAuthor().getEmail().equals(username)) {
            LOGGER.warn("#checkAccessToUpdateTaskForUser: no access to operate task by id {} for user by email {}. {}",
                    task.getId(), username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to operate this task: " + task.getId());
        }
    }

    private static void checkReleaseState(Task task) {
        LOGGER.info("#checkReleaseState: check tasks releases by id {}", task.getId());
        //ПРОВЕРЯЕМ РЕЛИЗЫ ЗАДАЧИ
        for (Release release : task.getReleases()) {
            checkReleaseEnd(task, release);
        }
    }

    private static void checkReleaseEnd(Task task, Release release) {
        LOGGER.info("#checkReleaseEnd: check end date for release by id {}", release.getId());
        //ПРОВЕРЯЕМ, ИМЕЕТ ЛИ РЕЛИЗ ВРЕМЯ ЗАВЕРШЕНИЯ
        if (release.getEnd() == null) {
            LOGGER.warn("#checkReleaseEnd: release by id {} in task by id {} is not completed. {}",
                    release.getId(), task.getId(), TaskException.class.getSimpleName());
            throw new TaskException("Cannot complete a task if one release is not completed");
        }
    }

    private static void checkProjectInProgress(Task task) {
        //ПРОВЕРЯЕМ, НАХОДИТСЯ ЛИ ПРОЕКТ В СТАТУС 'IN_PROGRESS'
        if (!task.getProject().getProjectState().equals(ProjectState.IN_PROGRESS)) {
            LOGGER.warn("#checkProjectInProgress: project by id {} is not running. {}", task.getProject().getId(),
                    TaskException.class.getSimpleName());
            throw new TaskException("Task can only be moved to a state 'IN_PROGRESS', when the project is running");
        }
    }

    private static void checkAccessProjectForUser(String username, Project project) {
        //ПРОВЕРЯЕМ, ЯВЛЯЕТСЯ ЛИ ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ СОЗДАТЕЛЕМ ПРОЕКТА
        if (!project.getOwner().getEmail().equals(username)) {
            LOGGER.warn("#checkAccessProjectForUser: no access to add task on project by id {}. {}", project.getId(),
                    AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to add task on this project: " + project.getId());
        }
    }

    private static void checkAccessTaskForUser(String username, Task task) {
        //ПРОВЕРЯЕМ, ИМЕЕТ ЛИ ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ ДОСТУП К ИЗМЕНЕНИЮ ЗАДАЧИ
        if (task.getDeveloper() == null || !task.getAuthor().getEmail().equals(username) && !task.getDeveloper().getEmail().equals(username)) {
            LOGGER.warn("#checkAccessTaskForUser: no access to update task by {} for user by email {}. {}",
                    task.getId(), username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("Only author or developer can change task state");
        }
    }

    private static void checkTaskInProgress(Task task) {
        //ПРОВЕРЯЕМ, НЕ НАХОДИТСЯ ЛИ ЗАДАЧА В СОСТОЯНИИ 'IN_PROGRESS'
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#checkTaskInProgress: task by id {} is not running. {}", task.getId(),
                    TaskException.class.getSimpleName());
            throw new TaskException("Task by id " + task.getId() + " is not running");
        }
    }

    private static void isTaskInProgress(Task task) {
        //ПРОВЕРЯЕМ, ЗАПУЩЕНА ЛИ ЗАДАЧА И ИМЕЕТ ЛИ ИСПОЛНИТЕЛЯ
        if (task.getDeveloper() != null || task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#isTaskInProgress: task by id {} already started. {}", task.getId(),
                    TaskException.class.getSimpleName());
            throw new TaskException("Task is running or done");
        }
    }

    @Transactional
    @Override
    public TaskResponse addTask(TaskRequest taskRequest, String username) {
        UUID projectId = taskRequest.getProjectId();
        Project project = getProjectById(projectId);
        checkAccessProjectForUser(username, project);
        Task newTask = createTask(taskRequest, project);
        newTask = saveTask(newTask);
        return taskMapper.toResponse(newTask);
    }

    @Transactional
    @Override
    public TaskResponse updateTask(TaskRequest taskRequest, UUID taskId, String username) {
        Task task = getTask(taskId);
        checkAccessToOperateTaskForUser(username, task);
        taskMapper.update(taskRequest, task);
        task = saveTask(task);
        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    @Override
    public TaskResponse getTaskById(UUID taskId, String username) {
        Task task = getTask(taskId);
        if (isUserProjectParticipantInverted(username, task)) {
            LOGGER.warn("#getTaskById: no access for task by id {} for user by email {}. {}",
                    task.getId(), username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access for this task: " + task.getId());
        }
        return taskMapper.toResponse(task);
    }

    @Transactional
    @Override
    public SuccessResponse deleteTaskById(UUID taskId, String username) {
        Task task = getTask(taskId);
        checkAccessToOperateTaskForUser(username, task);
        LOGGER.info("#deleteTaskById: try to delete task by id {}", taskId);
        taskRepository.delete(task);
        LOGGER.info("#deleteTaskById: task by id {} deleted", taskId);
        return SuccessResponse.builder()
                .message("Task deleted successfully")
                .time(Instant.now())
                .build();
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToBacklog(UUID taskId, String username) {
        Task task = getTask(taskId);
        checkTaskInProgress(task);
        checkAccessTaskForUser(username, task);
        checkReleaseState(task);
        task.setDeveloper(null);
        task.setTaskState(TaskState.BACKLOG);
        task = saveTask(task);
        return taskMapper.toResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToInProgress(UUID taskId, String username) {
        Task task = getTask(taskId);
        User user = getUserByUsername(username);
        checkProjectInProgress(task);
        isTaskInProgress(task);
        checkTaskParticipants(username, task);
        task.setDeveloper(user);
        task.setTaskState(TaskState.IN_PROGRESS);
        task = saveTask(task);
        return taskMapper.toResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToDone(UUID taskId, String username) {
        Task task = getTask(taskId);
        //НЕЛЬЗЯ СРАЗУ ПЕРЕВЕСТИ В СОСТОЯНИЕ 'DONE' ИЗ 'BACKLOG'
        checkTaskInProgress(task);
        checkAccessTaskForUser(username, task);
        checkReleaseState(task);
        task.setTaskState(TaskState.DONE);
        task = saveTask(task);
        return taskMapper.toResponse(task);
    }

    private Task saveTask(Task newTask) {
        LOGGER.info("#saveTask: try to save task");
        newTask = taskRepository.save(newTask);
        LOGGER.info("#saveTask: task by id {} saved", newTask.getId());
        return newTask;
    }

    private Task createTask(TaskRequest taskRequest, Project project) {
        Task newTask = taskMapper.toEntity(taskRequest);
        newTask.setProject(project);
        newTask.setAuthor(project.getOwner());
        newTask.setTaskState(TaskState.BACKLOG);
        return newTask;
    }

    private Project getProjectById(UUID projectId) {
        LOGGER.info("#getProjectById: find project by id {}", projectId);
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#getProjectById: project by id {} not found. {}", projectId,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Project by id " + projectId + " not found");
        }
        return optionalProject.get();
    }

    private Task getTask(UUID taskId) {
        LOGGER.info("#getTask: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#getTask: task by id {} not found. {}", taskId, DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        return optionalTask.get();
    }

    private void checkTaskParticipants(String username, Task task) {
        if (isUserProjectParticipantInverted(username, task)) {
            LOGGER.warn("#checkTaskParticipants: no access to operate task by id {} for user by email {}. {}",
                    task.getId(), username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to operate this task: " + task.getId());
        }
    }

    private boolean isUserProjectParticipantInverted(String username, Task task) {
        LOGGER.info("#isUserProjectParticipant: run");
        //ПРОВЕРЯЕМ, ЯВЛЯЕТСЯ ЛИ ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ УЧАСТНИКОМ ПРОЕКТА
        return task.getProject().getUsers().stream().noneMatch(u -> u.getEmail().equals(username));
    }

    private User getUserByUsername(String username) {
        LOGGER.info("#getUserByUsername: find user by email {}", username);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#getUserByUsername: user by email {} not found. {}", username,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("No found user by email " + username);
        }
        return optionalUser.get();
    }

}
