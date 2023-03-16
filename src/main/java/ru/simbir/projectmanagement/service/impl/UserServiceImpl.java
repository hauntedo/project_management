package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(UUID userId) {
        LOGGER.info("#getUserById: find user by id {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#getUserById: user by id {} not found", userId);
            return UserResponse.builder().build();
        }
        return userMapper.toResponse(optionalUser.get());
    }

    @Transactional
    @Override
    public UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest) {
        LOGGER.info("#updateUser: find user by id {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            LOGGER.warn("#updateUser: user by id {} not found. {}", userId,
                    DataNotFoundException.class.getSimpleName());
            throw new DataNotFoundException("No found user by id " + userId);
        }
        User user = optionalUser.get();
        userMapper.updateUser(userUpdateRequest, user);
        LOGGER.info("#updateUser: try to save user by id {}", userId);
        user = userRepository.save(user);
        LOGGER.info("#updateUser: user by id {} saved", userId);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProjectResponse> getProjectsByUserId(UUID userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        LOGGER.info("#getProjectsByUserId: find all projects by user id {}", userId);
        Page<Project> projectPage = projectRepository.findAllByUsers_Id(userId, pageRequest);
        return PageResponse.<ProjectResponse>builder()
                .content(projectMapper.toList(projectPage.getContent()))
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .build();
    }
}
