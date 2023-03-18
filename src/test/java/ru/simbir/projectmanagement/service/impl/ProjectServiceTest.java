package ru.simbir.projectmanagement.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.OccupiedDataException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.utils.enums.ProjectState;
import ru.simbir.projectmanagement.utils.enums.Role;
import ru.simbir.projectmanagement.utils.mapper.ProjectMapper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
            String username = "testuser@example.com";
            User user = User.builder()
                    .email(username)
                    .build();
            UserResponse userResponse = UserResponse.builder()
                    .email(user.getEmail())
                    .build();
            ProjectRequest projectRequest = ProjectRequest.builder()
                    .code("testcode")
                    .build();
            Project project = Project.builder()
                    .code("testcode")
                    .owner(user)
                    .projectState(ProjectState.BACKLOG)
                    .build();

            ProjectResponse projectResponse = ProjectResponse.builder()
                    .code(project.getCode())
                    .projectState(project.getProjectState().name())
                    .name(project.getName())
                    .owner(userResponse)
                    .build();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(projectRepository.existsByCode(anyString())).thenReturn(false);
            when(projectMapper.toEntity(projectRequest)).thenReturn(project);
            when(projectRepository.save(project)).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.createProject(projectRequest, username);

            verify(userRepository, times(1)).findByEmail(username);
            verify(projectRepository, times(1)).existsByCode(projectRequest.getCode());
            verify(projectRepository, times(1)).save(project);
            verify(projectMapper, times(1)).toEntity(projectRequest);
            verify(projectMapper, times(1)).toResponse(project);

            assertEquals(project.getCode(), result.getCode());
            assertEquals(project.getProjectState().name(), result.getProjectState());
            assertEquals(project.getOwner().getEmail(), result.getOwner().getEmail());
        }

        @Test
        void throw_occupied_data_exception() {
            User user = User.builder()
                    .email("username")
                    .build();
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(projectRepository.existsByCode(anyString())).thenReturn(Boolean.TRUE);
            assertThrows(OccupiedDataException.class, () -> projectService.createProject(ProjectRequest.builder()
                    .code("testcode")
                    .build(), "other")
            );
        }
    }

    @Test
    void updateProjectById() {
    }

    @Test
    void startProject() {
    }

    @Test
    void endProject() {
    }
}