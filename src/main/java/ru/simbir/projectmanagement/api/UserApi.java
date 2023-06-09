package ru.simbir.projectmanagement.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.simbir.projectmanagement.dto.request.UserUpdateRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequestMapping("/api/users")
public interface UserApi {

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/{user-id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<UserResponse> getUserById(@PathVariable("user-id") UUID userId);

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/{user-id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<UserResponse> updateUserById(@PathVariable("user-id") UUID userId,
                                                @RequestBody @Valid UserUpdateRequest userUpdateRequest);

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/{user-id}/projects", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<PageResponse<ProjectResponse>> getUserProjects(@RequestParam(value = "page", required = false) int page,
                                                                  @RequestParam(value = "size", required = false) int size,
                                                                  @PathVariable("user-id") UUID userId);


}
