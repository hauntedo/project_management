package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.ProjectApi;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.service.ProjectService;

import java.util.List;
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
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return null;
    }

    @Override
    public ResponseEntity<ProjectResponse> getProjectById(UUID projectId) {
        return null;
    }

    @Override
    public ResponseEntity<ProjectResponse> updateProjectById(ProjectRequest projectRequest, UUID projectId, UserDetails userDetails) {
        return ResponseEntity.status(201).body(projectService.updateProjectById(projectId, projectRequest, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<ProjectResponse> startProjectById(UUID projectId, UserDetails userDetails) {
        return ResponseEntity.ok(projectService.startProject(projectId, userDetails.getUsername()));
    }
}
