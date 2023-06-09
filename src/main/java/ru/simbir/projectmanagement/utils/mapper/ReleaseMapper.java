package ru.simbir.projectmanagement.utils.mapper;

import org.mapstruct.Mapper;
import ru.simbir.projectmanagement.dto.request.ReleaseRequest;
import ru.simbir.projectmanagement.dto.response.ReleaseResponse;
import ru.simbir.projectmanagement.model.Release;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ReleaseMapper {

    ReleaseResponse toResponse(Release release);

    Release toEntity(ReleaseRequest releaseRequest);

    List<ReleaseResponse> toList(Collection<Release> releases);
}
