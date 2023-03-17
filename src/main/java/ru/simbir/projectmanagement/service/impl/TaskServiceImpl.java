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

    private static void checkOperateAccess(String username, Task task) {
        //проверка на доступ над задачей
        if (!task.getAuthor().getEmail().equals(username)) {
            LOGGER.warn("#checkOperateAccess: no access to operate task by id {} for user by email {}. {}",
                    task.getId(), username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to operate this task: " + task.getId());
        }
    }

    private static void checkReleaseState(Task task) {
        //задачу нельзя завершить, если релизы не закрыты
        for (Release release : task.getReleases()) {
            if (release.getEnd() == null) {
                LOGGER.warn("#checkReleaseState: release by id {} in task by id {} is not completed. {}",
                        release.getId(), task.getId(), TaskException.class.getSimpleName());
                throw new TaskException("Cannot complete a task if one release is not completed");
            }
        }
    }

    private static void checkProjectState(Task task) {
        //проверка того, что проект находится в стадии выполнения
        if (!task.getProject().getProjectState().equals(ProjectState.IN_PROGRESS)) {
            LOGGER.warn("#checkProjectState: project by id {} is not running. {}", task.getProject().getId(),
                    TaskException.class.getSimpleName());
            throw new TaskException("Task can only be moved to a state 'IN_PROGRESS', when the project is running");
        }
    }

    @Transactional
    @Override
    public TaskResponse addTask(TaskRequest taskRequest, String username) {
        UUID projectId = taskRequest.getProjectId();
        LOGGER.info("#addTask: find project by id {}", projectId);
        Optional<Project> optionalProject = projectRepository.findById(taskRequest.getProjectId());
        if (!optionalProject.isPresent()) {
            LOGGER.warn("#addTask: project by id {} not found. {}", projectId,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Project by id " + projectId + " not found");
        }
        Project project = optionalProject.get();
        //задачу могут создавать лишь СОЗДАТЕЛИ проекта
        if (!project.getOwner().getEmail().equals(username)) {
            LOGGER.warn("#addTask: no access to add task on project by id {}. {}", projectId,
                    AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to add task on this project: " + project.getId());
        }
        Task newTask = taskMapper.toEntity(taskRequest);
        newTask.setProject(project);
        newTask.setAuthor(project.getOwner());
        newTask.setTaskState(TaskState.BACKLOG);
        LOGGER.info("#addTask: try to save task");
        newTask = taskRepository.save(newTask);
        LOGGER.info("#addTask: task by id {} saved", newTask.getId());
        return taskMapper.toResponse(newTask);
    }

    @Transactional
    @Override
    public TaskResponse updateTask(TaskRequest taskRequest, UUID taskId, String username) {
        LOGGER.info("#updateTask: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#updateTask: task by id {} not found. {}", taskId, DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        //проверка доступа над задачей
        checkOperateAccess(username, task);
        taskMapper.update(taskRequest, task);
        LOGGER.info("#updateTask: try to save task");
        task = taskRepository.save(task);
        LOGGER.info("#updateTask: task by id {} saved", task.getId());
        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    @Override
    public TaskResponse getTaskById(UUID taskId, String username) {
        LOGGER.info("#getTaskById: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#getTaskById: task by id {} not found.", taskId);
            return TaskResponse.builder().build();
        }
        Task task = optionalTask.get();
        //информацию о задаче может получить лишь участник проекта
        for (User u : task.getProject().getUsers()) {
            if (u.getEmail().equals(username)) {
                return taskMapper.toResponse(task);
            }
        }
        LOGGER.warn("#getTaskById: no access for task by id {} for user by email {}. {}",
                task.getId(), username, AccessDeniedException.class.getSimpleName());
        throw new AccessDeniedException("No access for this task: " + task.getId());
    }

    @Transactional
    @Override
    public SuccessResponse deleteTaskById(UUID taskId, String username) {
        LOGGER.info("#deleteTaskById: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#deleteTaskById: task by id {} not found", taskId);
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        //проверка доступа над задачей
        checkOperateAccess(username, task);
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
        LOGGER.info("#updateTaskToBacklog: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#updateTaskToBacklog: task by id {} not found", taskId);
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        //задачу нельзя отложить, если он не запущен( логично :) ) или завершен
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#updateTaskToBacklog: task by id {} is not running. {}", taskId,
                    TaskException.class.getSimpleName());
            throw new TaskException("Task by id " + taskId + " is not running");
        }
        //проверка состояния релиза
        checkReleaseState(task);
        //отложить задачу могут только исполнитель или автор задачи
        if (task.getAuthor().getEmail().equals(username) || task.getDeveloper().getEmail().equals(username)) {
            task.setDeveloper(null);
            //перевод в другое состояние
            task = transferTaskState(task, TaskState.BACKLOG);
            return taskMapper.toResponse(task);
        } else {
            LOGGER.warn("#updateTaskToBacklog: no access to update task by {} for user by email {}. {}",
                    taskId, username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("Only author or developer can change task state");
        }
    }

    private Task transferTaskState(Task task, TaskState taskState) {
        task.setTaskState(taskState);
        LOGGER.info("#saveTask: try to save task by id {}", task.getId());
        task = taskRepository.save(task);
        LOGGER.info("#saveTask: task by id {} saved", task.getId());
        return task;
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToInProgress(UUID taskId, String username) {
        LOGGER.info("#updateTaskToInProgress: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#updateTaskToInProgress: task by id {} not found", taskId);
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        LOGGER.info("#updateTaskToInProgress: find user by email {}", username);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#updateTaskToInProgress: user by email {} not found. {}", username,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("No found user by email " + username);
        }
        User user = optionalUser.get();
        //задачу можно запустить, только если проект запущен
        checkProjectState(task);
        //проверка того, имеет ли задача исполнителя или она запущена
        if (task.getDeveloper() != null || task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#updateTaskToInProgress: task by id {} already started. {}", taskId,
                    TaskException.class.getSimpleName());
            throw new TaskException("Task is running");
        }
        //задачу могут начать только участники проекта
        for (User u : task.getProject().getUsers()) {
            if (u.getEmail().equals(username)) {
                task.setDeveloper(user);
                //перевод в другое состояние
                task = transferTaskState(task, TaskState.IN_PROGRESS);
                return taskMapper.toResponse(task);
            }
        }
        LOGGER.warn("#updateTaskToInProgress: no access to operate task by id {} for user by email {}. {}",
                task.getId(), username, AccessDeniedException.class.getSimpleName());
        throw new AccessDeniedException("No access to operate this task: " + task.getId());

    }

    @Transactional
    @Override
    public TaskResponse updateTaskToDone(UUID taskId, String username) {
        LOGGER.info("#updateTaskToDone: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#updateTaskToDone: task by id {} not found", taskId);
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        //проверка состояния проекта
        checkProjectState(task);
        //нельзя сразу перевести в состояние 'DONE' из 'BACKLOG'
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#updateTaskToDone: task by id {} not started yet. {}", task.getId(),
                    TaskException.class.getSimpleName());
            throw new TaskException("Task by id " + task.getId() + " not started yet");
        }
        //только исполнитель либо автор задачи могут завершить ее
        if (task.getAuthor().getEmail().equals(username) || task.getDeveloper().getEmail().equals(username)) {
            //проверка состояния релиза
            checkReleaseState(task);
            //перевод в другое состояние
            task = transferTaskState(task, TaskState.DONE);
            return taskMapper.toResponse(task);
        } else {
            LOGGER.warn("#updupdateTaskToDone: no access to update task by {} for user by email {}. {}",
                    taskId, username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("Only author or developer can close task");
        }
    }

}
