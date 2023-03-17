package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.AdminApi;
import ru.simbir.projectmanagement.dto.response.*;
import ru.simbir.projectmanagement.service.AdminService;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

    private final AdminService adminService;

    @Override
    public ResponseEntity<PageResponse<TaskResponse>> getTasks(int page, int size) {
        return ResponseEntity.ok(adminService.getTasks(page, size));
    }

    @Override
    public ResponseEntity<PageResponse<ReleaseResponse>> getReleases(int page, int size) {
        return ResponseEntity.ok(adminService.getReleases(page, size));
    }

    @Override
    public ResponseEntity<PageResponse<ProjectResponse>> getProjects(int page, int size) {
        return ResponseEntity.ok(adminService.getProjects(page, size));
    }

    @Override
    public ResponseEntity<PageResponse<UserResponse>> getUsers(int page, int size) {
        return ResponseEntity.ok(adminService.getUsers(page, size));
    }

}
