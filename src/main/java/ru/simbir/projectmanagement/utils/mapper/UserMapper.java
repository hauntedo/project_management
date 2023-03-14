package ru.simbir.projectmanagement.utils.mapper;

import org.mapstruct.Mapper;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegistrationRequest registrationRequest);
    UserResponse toResponse(User user);
}
