package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.response.*;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.Release;
import ru.simbir.projectmanagement.model.Task;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.ReleaseRepository;
import ru.simbir.projectmanagement.repository.TaskRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.AdminService;
import ru.simbir.projectmanagement.utils.mapper.ProjectMapper;
import ru.simbir.projectmanagement.utils.mapper.ReleaseMapper;
import ru.simbir.projectmanagement.utils.mapper.TaskMapper;
import ru.simbir.projectmanagement.utils.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LogManager.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ReleaseRepository releaseRepository;
    private final ReleaseMapper releaseMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<TaskResponse> getTasks(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getTasks: find all tasks");
        Page<Task> taskPage = taskRepository.findAll(pageRequest);
        return PageResponse.<TaskResponse>builder()
                .content(taskMapper.toList(taskPage.getContent()))
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ReleaseResponse> getReleases(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getReleases: find all releases");
        Page<Release> releasePage = releaseRepository.findAll(pageRequest);
        return PageResponse.<ReleaseResponse>builder()
                .content(releaseMapper.toList(releasePage.getContent()))
                .totalElements(releasePage.getTotalElements())
                .totalPages(releasePage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProjectResponse> getProjects(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getProjects: find all projects");
        Page<Project> projectPage = projectRepository.findAll(pageRequest);
        return PageResponse.<ProjectResponse>builder()
                .content(projectMapper.toList(projectPage.getContent()))
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserResponse> getUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getUsers: find all users");
        Page<User> userPage = userRepository.findAll(pageRequest);
        return PageResponse.<UserResponse>builder()
                .content(userMapper.toList(userPage.getContent()))
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }
}
