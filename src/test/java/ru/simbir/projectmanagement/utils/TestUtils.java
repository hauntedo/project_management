package ru.simbir.projectmanagement.utils;

import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.Task;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.utils.enums.ProjectState;
import ru.simbir.projectmanagement.utils.enums.Role;
import ru.simbir.projectmanagement.utils.enums.TaskState;

import java.util.UUID;

public class TestUtils {

    public static User getUser() {
        return User.builder()
                .email("test@test.test")
                .password("qwerty")
                .fullName("test")
                .role(Role.USER)
                .id(UUID.randomUUID())
                .build();
    }

    public static Project getProjectFromRequest(User user, ProjectRequest projectRequest) {
        return Project.builder()
                .code(projectRequest.getCode())
                .owner(user)
                .name(projectRequest.getName())
                .projectState(ProjectState.BACKLOG)
                .id(UUID.randomUUID())
                .build();
    }

    public static Project getProject(User user) {
        return Project.builder()
                .code("testcode")
                .owner(user)
                .name("testname")
                .projectState(ProjectState.BACKLOG)
                .id(UUID.randomUUID())
                .build();
    }

    public static ProjectResponse getProjectResponse(Project project, UserResponse userResponse) {
        return ProjectResponse.builder()
                .code(project.getCode())
                .id(project.getId())
                .projectState(project.getProjectState().name())
                .name(project.getName())
                .owner(userResponse)
                .build();
    }

    public static UserResponse getUserResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .id(user.getId())
                .build();
    }

    public static Task getTask(Project project, User user) {
        return Task.builder()
                .id(UUID.randomUUID())
                .project(project)
                .author(user)
                .name("testtask")
                .taskState(TaskState.BACKLOG)
                .build();
    }

}
