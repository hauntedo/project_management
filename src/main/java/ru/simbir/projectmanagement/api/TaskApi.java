package ru.simbir.projectmanagement.api;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.SuccessResponse;
import ru.simbir.projectmanagement.dto.response.TaskResponse;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/tasks")
public interface TaskApi {

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<TaskResponse> addTask(@RequestBody @Valid TaskRequest taskRequest,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);

    @PutMapping(value = "/{task-id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<TaskResponse> updateTaskById(@RequestBody @Valid TaskRequest taskRequest,
                                                @PathVariable("task-id") UUID taskId,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);


    @GetMapping(value = "/{task-id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TaskResponse> getTaskById(@PathVariable("task-id") UUID taskId);

    @DeleteMapping(value = "/{task-id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> deleteTaskById(@PathVariable("task-id") UUID taskId,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);

    @PutMapping(value = "/{task-id}/backlog", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TaskResponse> updateBacklogTask(@PathVariable("task-id") UUID taskId,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);

    @PutMapping(value = "/{task-id}/in-progress", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TaskResponse> updateInProgressTask(@PathVariable("task-id") UUID taskId,
                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);

    @PutMapping(value = "/{task-id}/done", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TaskResponse> updateDoneTask(@PathVariable("task-id") UUID taskId,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);

}
