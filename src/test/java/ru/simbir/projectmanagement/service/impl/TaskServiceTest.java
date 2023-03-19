package ru.simbir.projectmanagement.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.exception.DataNotFoundException;
import ru.simbir.projectmanagement.model.Project;
import ru.simbir.projectmanagement.model.Task;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.ProjectRepository;
import ru.simbir.projectmanagement.repository.TaskRepository;
import ru.simbir.projectmanagement.utils.TestUtils;
import ru.simbir.projectmanagement.utils.enums.TaskState;
import ru.simbir.projectmanagement.utils.mapper.TaskMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("taskService is working")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Nested
    @DisplayName("addTask is working")
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class addTask {

        @Test
        void add_task() {
            //given
            User user = TestUtils.getUser();
            UserResponse userResponse = TestUtils.getUserResponse(user);
            Project project = TestUtils.getProject(user);
            TaskRequest taskRequest = TaskRequest.builder()
                    .name("testtask")
                    .projectId(project.getId())
                    .build();
            Task task = TestUtils.getTask(project, user);
            TaskResponse taskResponse = TestUtils.getTaskResponse(userResponse, task);
            TaskResponse expected = TaskResponse.builder()
                    .taskState(TaskState.BACKLOG.name())
                    .id(task.getId())
                    .name("testtask")
                    .author(userResponse)
                    .build();
            when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
            when(taskMapper.toEntity(taskRequest)).thenReturn(task);
            when(taskRepository.save(task)).thenReturn(task);
            when(taskMapper.toResponse(task)).thenReturn(taskResponse);

            //when
            TaskResponse actual = taskService.addTask(taskRequest, user.getEmail());

            //then
            assertEquals(expected, actual);
        }

        @Test
        void throw_data_not_found_exception() {
            //given
            UUID projectId = UUID.randomUUID();
            TaskRequest taskRequest = TaskRequest.builder()
                    .name("testtask")
                    .projectId(projectId)
                    .build();
            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            //when & then
            assertThrows(DataNotFoundException.class, () -> taskService.addTask(taskRequest, anyString()));
        }

        @Test
        void throw_access_denied_exception() {
            //given
            User user = TestUtils.getUser();
            Project project = TestUtils.getProject(user);
            TaskRequest taskRequest = TaskRequest.builder()
                    .name("testtask")
                    .projectId(project.getId())
                    .build();
            when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

            assertThrows(AccessDeniedException.class, () -> taskService.addTask(taskRequest, anyString()));

        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("updateTask is working")
    class updateTask {

        @Test
        void update_task() {
            //given
            User user = TestUtils.getUser();
            UserResponse userResponse = TestUtils.getUserResponse(user);
            Project project = TestUtils.getProject(user);
            TaskRequest taskRequest = TaskRequest.builder()
                    .name("updatedtesttask")
                    .projectId(project.getId())
                    .build();
            Task task = TestUtils.getTask(project, user);
            task.setName(taskRequest.getName());
            TaskResponse taskResponse = TestUtils.getTaskResponse(userResponse, task);
            String expected = "updatedtesttask";
            when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
            doNothing().when(taskMapper).update(taskRequest, task);
            when(taskRepository.save(task)).thenReturn(task);
            when(taskMapper.toResponse(task)).thenReturn(taskResponse);

            //when
            TaskResponse actual = taskService.updateTask(taskRequest, task.getId(), user.getEmail());

            //then
            assertEquals(expected, actual.getName());

        }

        @Test
        void throw_access_denied_exception() {
            //given
            User user = TestUtils.getUser();
            Project project = TestUtils.getProject(user);
            Task task = TestUtils.getTask(project, user);
            TaskRequest taskRequest = TaskRequest.builder()
                    .name("updatedtesttask")
                    .projectId(project.getId())
                    .build();
            when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

            //when & then
            assertThrows(AccessDeniedException.class, () -> taskService.updateTask(taskRequest, task.getId(), anyString()));

        }

        @Test
        void throw_data_not_found_exception() {
            //given
            UUID taskId = UUID.randomUUID();
            TaskRequest taskRequest = TaskRequest.builder()
                    .name("updatedtesttask")
                    .build();
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            //when & then
            assertThrows(DataNotFoundException.class, () -> taskService.updateTask(taskRequest, taskId, anyString()));
        }

    }

}