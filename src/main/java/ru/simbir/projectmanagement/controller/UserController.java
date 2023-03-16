package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.UserApi;
import ru.simbir.projectmanagement.dto.request.UserUpdateRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.service.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserResponse> updateUserById(UUID userId, UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.status(201).body(userService.updateUser(userId, userUpdateRequest));
    }

    @Override
    public ResponseEntity<PageResponse<ProjectResponse>> getUserProjects(int page, int size, UUID userId) {
        return ResponseEntity.ok(userService.getProjectsByUserId(userId, page, size));
    }

}
