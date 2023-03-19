package ru.simbir.projectmanagement.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.simbir.projectmanagement.dto.request.ProjectRequest;
import ru.simbir.projectmanagement.dto.response.ProjectResponse;
import ru.simbir.projectmanagement.model.Project;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TaskMapper.class})
public interface ProjectMapper {

    ProjectResponse toResponse(Project project);

    Project toEntity(ProjectRequest projectRequest);

    void update(ProjectRequest projectRequest, @MappingTarget Project project);

    List<ProjectResponse> toList(Collection<Project> projects);

}
