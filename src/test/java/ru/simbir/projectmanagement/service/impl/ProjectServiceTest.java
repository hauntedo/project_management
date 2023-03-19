package ru.simbir.projectmanagement.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.OccupiedDataException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.utils.enums.ProjectState;
import ru.simbir.projectmanagement.utils.mapper.ProjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService is working")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("createProject is working")
    class createProject {

        @Test
        void create_project() {
            String username = "test@test.test";
            User user = User.builder()
                    .email(username)
                    .build();
            UserResponse userResponse = UserResponse.builder()
                    .email(user.getEmail())
                    .build();
            ProjectRequest projectRequest = ProjectRequest.builder()
                    .code("testcode")
                    .name("testname")
                    .build();
            Project project = Project.builder()
                    .code("testcode")
                    .owner(user)
                    .name(projectRequest.getName())
                    .build();
            Project projectAfterSaving = project;
            projectAfterSaving.setId(UUID.fromString("adadd510-c505-11ed-afa1-0242ac120002"));
            projectAfterSaving.setProjectState(ProjectState.BACKLOG);

            ProjectResponse projectResponse = ProjectResponse.builder()
                    .code(projectAfterSaving.getCode())
                    .projectState(projectAfterSaving.getProjectState().name())
                    .name(projectAfterSaving.getName())
                    .owner(userResponse)
                    .build();


            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(projectRepository.existsByCode(anyString())).thenReturn(false);
            when(projectMapper.toEntity(projectRequest)).thenReturn(project);
            when(projectRepository.save(project)).thenReturn(projectAfterSaving);
            when(projectMapper.toResponse(projectAfterSaving)).thenReturn(projectResponse);

            ProjectResponse expected = ProjectResponse.builder()
                    .code("testcode")
                    .projectState(ProjectState.BACKLOG.name())
                    .name("testname")
                    .owner(UserResponse.builder()
                            .email("test@test.test")
                            .build())
                    .build();

            ProjectResponse actual = projectService.createProject(projectRequest, username);

            assertEquals(expected, actual);
        }

        @Test
        void throw_occupied_data_exception() {
            User user = User.builder()
                    .email("username")
                    .build();
            when(userRepository.findByEmail("username")).thenReturn(Optional.of(user));
            when(projectRepository.existsByCode(anyString())).thenReturn(Boolean.TRUE);
            assertThrows(OccupiedDataException.class, () -> projectService.createProject(ProjectRequest.builder()
                    .code("testcode")
                    .build(), "username")
            );
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("updateProjectById is working")
    class updateProjectById {

        @Test
        void update_project_by_id() {
            UUID uuid = UUID.randomUUID();
            String username = "test@test.test";
            User user = User.builder()
                    .email(username)
                    .build();
            ProjectRequest projectRequest = ProjectRequest.builder()
                    .code("updatecode")
                    .name("updatename")
                    .build();
            Project project = Project.builder()
                    .id(uuid)
                    .code("testcode")
                    .name("testname")
                    .owner(user)
                    .build();
            Project afterSaving = Project.builder()
                    .id(project.getId())
                    .code(projectRequest.getCode())
                    .name(projectRequest.getName())
                    .owner(project.getOwner())
                    .build();
            UserResponse userResponse = UserResponse.builder()
                    .email(user.getEmail())
                    .build();
            ProjectResponse projectResponse = ProjectResponse.builder()
                    .id(afterSaving.getId())
                    .code(afterSaving.getCode())
                    .name(afterSaving.getName())
                    .owner(userResponse)
                    .build();
            when(projectRepository.findById(uuid)).thenReturn(Optional.of(project));
            doNothing().when(projectMapper).update(projectRequest, project);
            when(projectRepository.save(project)).thenReturn(afterSaving);
            when(projectMapper.toResponse(afterSaving)).thenReturn(projectResponse);

            ProjectResponse expected = ProjectResponse.builder()
                    .name("updatename")
                    .code("updatecode")
                    .id(uuid)
                    .owner(userResponse)
                    .build();

            ProjectResponse actual = projectService.updateProjectById(uuid, projectRequest, username);

            verify(projectMapper, times(1)).update(projectRequest, project);

            assertEquals(expected, actual);


        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("startProject is working")
    class startProject {

        @Test
        void start_project() {
            UUID uuid = UUID.randomUUID();
            String username = "test@test.test";
            User user = User.builder()
                    .email(username)
                    .build();
            UserResponse userResponse = UserResponse.builder()
                    .email(user.getEmail())
                    .build();
            Project project = Project.builder()
                    .id(uuid)
                    .projectState(ProjectState.BACKLOG)
                    .owner(user)
                    .build();
            Project runProject = Project.builder()
                    .id(project.getId())
                    .owner(project.getOwner())
                    .projectState(ProjectState.IN_PROGRESS)
                    .build();
            when(projectRepository.findById(uuid)).thenReturn(Optional.of(project));
            when(projectRepository.save(project)).thenReturn(runProject);
            ProjectResponse projectResponse = ProjectResponse.builder()
                    .owner(userResponse)
                    .projectState(runProject.getProjectState().name())
                    .id(project.getId())
                    .build();
            when(projectMapper.toResponse(runProject)).thenReturn(projectResponse);

            ProjectResponse expected = ProjectResponse.builder()
                    .id(uuid)
                    .projectState(ProjectState.IN_PROGRESS.name())
                    .owner(userResponse)
                    .build();

            ProjectResponse actual = projectService.startProject(uuid, username);
            assertEquals(expected, actual);

        }
    }
}