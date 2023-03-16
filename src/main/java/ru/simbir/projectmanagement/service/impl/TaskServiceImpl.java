package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
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

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    private static void checkOperateAccess(String username, Task task) {
        if (!task.getAuthor().getEmail().equals(username)) {
            throw new AccessDeniedException("No access to operate this task: " + task.getId());
        }
    }

    private static void checkReleaseState(Task task) {
        for (Release release : task.getReleases()) {
            if (release.getEnd() == null) {
                throw new TaskExecutionException("Cannot complete a task if one release is not completed");
            }
        }
    }

    @Transactional
    @Override
    public TaskResponse addTask(TaskRequest taskRequest, String username) {
        Project project = projectRepository.findById(taskRequest.getProjectId()).orElseThrow(DataNotFoundException::new);
        if (!project.getOwner().getEmail().equals(username)) {
            throw new AccessDeniedException("No access to add task on this project: " + project.getId());
        }
        Task newTask = taskMapper.toEntity(taskRequest);
        newTask.setProject(project);
        newTask.setAuthor(project.getOwner());
        newTask.setTaskState(TaskState.BACKLOG);
        return taskMapper.toResponse(taskRepository.save(newTask));
    }

    @Transactional
    @Override
    public TaskResponse updateTask(TaskRequest taskRequest, UUID taskId, String username) {
        Task task = getTask(taskId);
        checkOperateAccess(username, task);
        taskMapper.update(taskRequest, task);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    @Override
    public TaskResponse getTaskById(UUID taskId) {
        return taskMapper.toResponse(getTask(taskId));
    }

    @Transactional
    @Override
    public void deleteTaskById(UUID taskId, String username) {
        Task task = getTask(taskId);
        checkOperateAccess(username, task);
        taskRepository.delete(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToBacklog(UUID taskId, String username) {
        Task task = getTask(taskId);
        if (!task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            throw new TaskExecutionException("Task is not running");
        }
        if (task.getAuthor().getEmail().equals(username) || task.getDeveloper().getEmail().equals(username)) {
            task.setTaskState(TaskState.BACKLOG);
            return taskMapper.toResponse(taskRepository.save(task));
        } else {
            throw new AccessDeniedException("Only author or developer can change task state");
        }
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToInProgress(UUID taskId, String username) {
        Task task = getTask(taskId);
        User user = userRepository.findByEmail(username).orElseThrow(DataNotFoundException::new);
        if (!task.getProject().getProjectState().equals(ProjectState.IN_PROGRESS)) {
            throw new TaskExecutionException("Task can only be moved to a state 'IN_PROGRESS', when the project is running");
        }
        if (task.getDeveloper() != null || task.getTaskState().equals(TaskState.IN_PROGRESS)) {
            throw new TaskExecutionException("Task is running");
        }
        task.setDeveloper(user);
        task.setTaskState(TaskState.IN_PROGRESS);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponse updateTaskToDone(UUID taskId, String username) {
        Task task = getTask(taskId);
        if (task.getAuthor().getEmail().equals(username) || task.getDeveloper().getEmail().equals(username)) {
            checkReleaseState(task);
            task.setTaskState(TaskState.DONE);
            return taskMapper.toResponse(taskRepository.save(task));
        } else {
            throw new AccessDeniedException("Only author or developer can close task");
        }
    }

    private Task getTask(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(DataNotFoundException::new);
    }
}
