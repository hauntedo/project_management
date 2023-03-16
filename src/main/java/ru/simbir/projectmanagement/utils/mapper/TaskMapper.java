package ru.simbir.projectmanagement.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.simbir.projectmanagement.dto.request.TaskRequest;
import ru.simbir.projectmanagement.dto.response.TaskResponse;
import ru.simbir.projectmanagement.model.Task;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = ReleaseMapper.class)
public interface TaskMapper {

    TaskResponse toResponse(Task task);

    Task toEntity(TaskRequest taskRequest);

    void update(TaskRequest taskRequest, @MappingTarget Task task);

    List<TaskResponse> toList(Collection<Task> tasks);

}
