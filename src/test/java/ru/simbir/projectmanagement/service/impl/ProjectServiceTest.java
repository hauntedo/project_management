package ru.simbir.projectmanagement.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.EntityStateException;
import ru.simbir.projectmanagement.exception.OccupiedDataException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.utils.TestUtils;
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

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("createProject is working")
    class createProject {

        @Test
        void create_project() {
            //given
            User user = TestUtils.getUser();
            UserResponse userResponse = TestUtils.getUserResponse(user);
            ProjectRequest projectRequest = ProjectRequest.builder()
                    .code("testcode")
                    .name("testname")
                    .build();
            Project project = TestUtils.getProjectFromRequest(user, projectRequest);
            UUID projectId = UUID.randomUUID();
            project.setId(projectId);
            project.setProjectState(ProjectState.BACKLOG);
            ProjectResponse projectResponse = TestUtils.getProjectResponse(project, userResponse);

            ProjectResponse expected = ProjectResponse.builder()
                    .code("testcode")
                    .projectState(ProjectState.BACKLOG.name())
                    .name("testname")
                    .id(projectId)
                    .owner(userResponse)
                    .build();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(projectRepository.existsByCode(anyString())).thenReturn(false);
            when(projectMapper.toEntity(projectRequest)).thenReturn(project);
            when(projectRepository.save(project)).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            //when
            ProjectResponse actual = projectService.createProject(projectRequest, user.getEmail());

            //then
            assertEquals(expected, actual);
        }

        @Test
        void throw_occupied_data_exception() {
            //given
            User user = TestUtils.getUser();

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(projectRepository.existsByCode("testcode")).thenReturn(Boolean.TRUE);

            //when & then
            assertThrows(OccupiedDataException.class, () -> projectService.createProject(
                    ProjectRequest.builder()
                    .code("testcode")
                    .build(),
                    user.getEmail())
            );
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("updateProjectById is working")
    class updateProjectById {

        @Test
        void update_project_by_id() {
            //given
            User user = TestUtils.getUser();
            UserResponse userResponse = TestUtils.getUserResponse(user);
            ProjectRequest projectRequest = ProjectRequest.builder()
                    .code("updatecode")
                    .name("updatename")
                    .build();
            Project project = TestUtils.getProjectFromRequest(user, projectRequest);

            ProjectResponse expected = ProjectResponse.builder()
                    .name("updatename")
                    .code("updatecode")
                    .id(project.getId())
                    .projectState(project.getProjectState().name())
                    .owner(userResponse)
                    .build();

            ProjectResponse projectResponse = TestUtils.getProjectResponse(project, userResponse);

            when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
            doNothing().when(projectMapper).update(projectRequest, project);
            when(projectRepository.save(project)).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            //when
            ProjectResponse actual = projectService.updateProjectById(project.getId(), projectRequest, user.getEmail());

            //then
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
            //given
            User user = TestUtils.getUser();
            UserResponse userResponse = TestUtils.getUserResponse(user);
            Project project = TestUtils.getProject(user);
            Project started = TestUtils.getProject(user);
            started.setProjectState(ProjectState.IN_PROGRESS);
            ProjectResponse projectResponse = TestUtils.getProjectResponse(started, userResponse);
            when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
            when(projectRepository.save(project)).thenReturn(started);
            when(projectMapper.toResponse(started)).thenReturn(projectResponse);

            ProjectResponse expected = ProjectResponse.builder()
                    .id(project.getId())
                    .projectState(ProjectState.IN_PROGRESS.name())
                    .owner(userResponse)
                    .build();

            //when
            ProjectResponse actual = projectService.startProject(project.getId(), user.getEmail());

            //then
            assertEquals(expected.getProjectState(), actual.getProjectState());

        }

        @Test
        void throw_entity_state_exception() {
            User user = TestUtils.getUser();
            Project project = TestUtils.getProject(user);
            project.setProjectState(ProjectState.IN_PROGRESS);
            when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

            assertThrows(EntityStateException.class, () -> projectService.startProject(project.getId(), user.getEmail())
                    );
        }
    }
}