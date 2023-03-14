package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;

import java.util.UUID;

public interface ProjectService {


    ProjectResponse createProject(ProjectRequest projectRequest, String username);

    ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username);
}
