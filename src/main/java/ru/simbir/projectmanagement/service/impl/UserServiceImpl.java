package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.UserUpdateRequest;
import ru.simbir.projectmanagement.dto.response.PageResponse;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.UserService;
import ru.simbir.projectmanagement.utils.mapper.ProjectMapper;
import ru.simbir.projectmanagement.utils.mapper.UserMapper;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(UUID userId) {
        return userMapper.toResponse(userRepository.findById(userId).orElseThrow(DataNotFoundException::new));
    }

    @Transactional
    @Override
    public UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        userMapper.updateUser(userUpdateRequest, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProjectResponse> getProjectsByUserId(UUID userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findAllByUsers_Id(userId, pageRequest);
        return PageResponse.<ProjectResponse>builder()
                .content(projectMapper.toList(projectPage.getContent()))
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .build();
    }
}
