package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.UserUpdateRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {


    UserResponse getUserById(UUID userId);

    UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest);

    PageResponse<ProjectResponse> getProjectsByUserId(UUID userId, int page, int size);
}
