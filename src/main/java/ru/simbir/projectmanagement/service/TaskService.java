package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.TaskResponse;

import java.util.UUID;

public interface TaskService {
    TaskResponse addTask(TaskRequest taskRequest, String username);

    TaskResponse updateTask(TaskRequest taskRequest, UUID taskId, String username);

    TaskResponse getTaskById(UUID taskId);

    void deleteTaskById(UUID taskId, String username);

    TaskResponse updateTaskToBacklog(UUID taskId, String username);

    TaskResponse updateTaskToInProgress(UUID taskId, String username);

    TaskResponse updateTaskToDone(UUID taskId, String username);
}
