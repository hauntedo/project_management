package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.TaskResponse;

import java.util.UUID;

public interface ProjectService {


    ProjectResponse createProject(ProjectRequest projectRequest, String username);

    ProjectResponse updateProjectById(UUID projectId, ProjectRequest projectRequest, String username);

    ProjectResponse startProject(UUID projectId, String username);

    ProjectResponse endProject(UUID projectId, String username);

    PageResponse<ProjectResponse> getProjects(int page, int size);

    ProjectResponse getProjectById(UUID projectId, String username);

    PageResponse<TaskResponse> getTasksByProjectId(UUID projectId, int page, int size);
}
