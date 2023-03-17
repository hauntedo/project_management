package ru.simbir.projectmanagement.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.simbir.projectmanagement.dto.response.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/admins")
public interface AdminApi {

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/tasks", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<PageResponse<TaskResponse>> getTasks(@RequestParam(value = "page", required = false) int page,
                                                        @RequestParam(value = "size", required = false) int size);

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/releases", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<PageResponse<ReleaseResponse>> getReleases(@RequestParam(value = "page", required = false) int page,
                                                              @RequestParam(value = "size", required = false) int size);

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/projects", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<PageResponse<ProjectResponse>> getProjects(@RequestParam(value = "page", required = false) int page,
                                                              @RequestParam(value = "size", required = false) int size);

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<PageResponse<UserResponse>> getUsers(@RequestParam(value = "page", required = false) int page,
                                                        @RequestParam(value = "size", required = false) int size);


}
