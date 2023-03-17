package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.ProjectApi;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.*;
import ru.simbir.projectmanagement.service.ProjectService;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;


    @Override
    public ResponseEntity<ProjectResponse> createProject(ProjectRequest projectRequest, UserDetails userDetails) {
        return ResponseEntity.status(201).body(projectService.createProject(projectRequest, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<PageResponse<ProjectResponse>> getProjects(int page, int size) {
        return ResponseEntity.ok(projectService.getProjects(page, size));
    }

    @Override
    public ResponseEntity<ProjectResponse> getProjectById(UUID projectId, UserDetails userDetails) {
        return ResponseEntity.ok(projectService.getProjectById(projectId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<ProjectResponse> updateProjectById(ProjectRequest projectRequest, UUID projectId, UserDetails userDetails) {
        return ResponseEntity.status(201).body(projectService.updateProjectById(projectId, projectRequest, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<ProjectResponse> startProjectById(UUID projectId, UserDetails userDetails) {
        return ResponseEntity.ok(projectService.startProject(projectId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<ProjectResponse> endProjectById(UUID projectId, UserDetails userDetails) {
        return ResponseEntity.ok(projectService.endProject(projectId, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<PageResponse<TaskResponse>> getProjectTasks(UUID projectId, int page, int size) {
        return ResponseEntity.ok(projectService.getTasksByProjectId(projectId, page, size));
    }

    @Override
    public ResponseEntity<PageResponse<UserResponse>> getUsersByProjectId(UUID projectId, int page, int size) {
        return ResponseEntity.ok(projectService.getUsersByProjectId(projectId, page, size));
    }

    @Override
    public ResponseEntity<SuccessResponse> joinProjectByCode(String projectCode, UserDetails userDetails) {
        return ResponseEntity.ok(projectService.joinProjectByCode(projectCode, userDetails.getUsername()));
    }
}
