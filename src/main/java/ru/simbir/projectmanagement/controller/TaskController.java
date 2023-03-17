package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.TaskApi;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.SuccessResponse;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.service.TaskService;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskResponse> addTask(TaskRequest taskRequest, UserDetails userDetails) {
        return ResponseEntity.status(201).body(taskService.addTask(taskRequest, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<TaskResponse> updateTaskById(TaskRequest taskRequest, UUID taskId, UserDetails userDetails) {
        return ResponseEntity.status(201).body(taskService.updateTask(taskRequest, taskId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<TaskResponse> getTaskById(UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @Override
    public ResponseEntity<SuccessResponse> deleteTaskById(UUID taskId, UserDetails userDetails) {
        return ResponseEntity.ok(taskService.deleteTaskById(taskId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<TaskResponse> updateBacklogTask(UUID taskId, UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTaskToBacklog(taskId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<TaskResponse> updateInProgressTask(UUID taskId, UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTaskToInProgress(taskId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<TaskResponse> updateDoneTask(UUID taskId, UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTaskToDone(taskId, userDetails.getUsername()));
    }


}
