package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.exception.TaskExecutionException;
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
        if (!task.getAuthor().getEmail().equals(username)) {
            LOGGER.warn("#checkOperateAccess: no access to operate task by id {} for user by email {}. {}",
                    task.getId(), username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("No access to operate this task: " + task.getId());
        }
    }

    private static void checkReleaseState(Task task) {
        for (Release release : task.getReleases()) {
            if (release.getEnd() == null) {
                LOGGER.warn("#checkReleaseState: release by id {} in task by id {} is not completed. {}",
                        release.getId(), task.getId(), TaskExecutionException.class.getSimpleName());
                throw new TaskExecutionException("Cannot complete a task if one release is not completed");
            }
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
        checkOperateAccess(username, task);
        taskMapper.update(taskRequest, task);
        LOGGER.info("#updateTask: try to save task");
        task = taskRepository.save(task);
        LOGGER.info("#updateTask: task by id {} saved", task.getId());
        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    @Override
    public TaskResponse getTaskById(UUID taskId) {
        LOGGER.info("#getTaskById: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#getTaskById: task by id {} not found.", taskId);
            return TaskResponse.builder().build();
        }
        return taskMapper.toResponse(optionalTask.get());
    }

    @Transactional
    @Override
    public void deleteTaskById(UUID taskId, String username) {
        LOGGER.info("#deleteTaskById: find task by id {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            LOGGER.warn("#deleteTaskById: task by id {} not found", taskId);
            throw new DataNotFoundException("Task by id " + taskId + " not found");
        }
        Task task = optionalTask.get();
        checkOperateAccess(username, task);
        LOGGER.info("#deleteTaskById: try to delete task by id {}", taskId);
        taskRepository.delete(task);
        LOGGER.info("#deleteTaskById: task by id {} deleted", taskId);

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
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#updateTaskToBacklog: task by id {} is not running. {}", taskId,
                    TaskExecutionException.class.getSimpleName());
            throw new TaskExecutionException("Task by id " + taskId + " is not running");
        }
        if (task.getAuthor().getEmail().equals(username) || task.getDeveloper().getEmail().equals(username)) {
            task.setTaskState(TaskState.BACKLOG);
            LOGGER.info("#updateTaskToBacklog: try to save task by id {}", taskId);
            task = taskRepository.save(task);
            LOGGER.info("#updateTaskToBacklog: task by id {} saved", task.getId());
            return taskMapper.toResponse(task);
        } else {
            LOGGER.warn("#updateTaskToBacklog: no access to update task by {} for user by email {}. {}",
                    taskId, username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("Only author or developer can change task state");
        }
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
        if (!task.getProject().getProjectState().equals(ProjectState.IN_PROGRESS)) {
            LOGGER.warn("#updateTaskToInProgress: task by id {} has not state IN_PROGRESS. {}", taskId,
                    TaskExecutionException.class.getSimpleName());
            throw new TaskExecutionException("Task can only be moved to a state 'IN_PROGRESS', when the project is running");
        }
        if (task.getDeveloper() != null || task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            LOGGER.warn("#updateTaskToInProgress: task by id {} already started. {}", taskId,
                    TaskExecutionException.class.getSimpleName());
            throw new TaskExecutionException("Task is running");
        }
        task.setDeveloper(user);
        task.setTaskState(TaskState.IN_PROGRESS);
        LOGGER.info("#updateTaskToInProgress: try to save task by id {}", taskId);
        task = taskRepository.save(task);
        LOGGER.info("#updateTaskToInProgress: task by id {} saved", task.getId());
        return taskMapper.toResponse(task);
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
        if (task.getAuthor().getEmail().equals(username) || task.getDeveloper().getEmail().equals(username)) {
            checkReleaseState(task);
            task.setTaskState(TaskState.DONE);
            LOGGER.info("#updateTaskToDone: try to save task  by id {}", taskId);
            task = taskRepository.save(task);
            LOGGER.info("#updateTaskToDone: task by id {} saved", task.getId());
            return taskMapper.toResponse(task);
        } else {
            LOGGER.warn("#updupdateTaskToDone: no access to update task by {} for user by email {}. {}",
                    taskId, username, AccessDeniedException.class.getSimpleName());
            throw new AccessDeniedException("Only author or developer can close task");
        }
    }

}
