package ru.simbir.projectmanagement.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/projects")
public interface ProjectApi {

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest,
                                                  @AuthenticationPrincipal UserDetails userDetails);

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ProjectResponse>> getProjects();

    @GetMapping(value = "/{project-id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ProjectResponse> getProjectById(@PathVariable("project-id") UUID projectId);

    @PutMapping(value = "/{project-id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<ProjectResponse> updateProjectById(@RequestBody @Valid ProjectRequest projectRequest,
                                                      @PathVariable("project-id") UUID projectId,
                                                      @AuthenticationPrincipal UserDetails userDetails);

    @GetMapping(value = "/{project-id}/start", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ProjectResponse> startProjectById(@PathVariable("project-id") UUID projectId,
                                                     @AuthenticationPrincipal UserDetails userDetails);

    @GetMapping(value = "/{project-id}/end", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ProjectResponse> endProjectById(@PathVariable("project-id") UUID projectId,
                                                   @AuthenticationPrincipal UserDetails userDetails);
}
