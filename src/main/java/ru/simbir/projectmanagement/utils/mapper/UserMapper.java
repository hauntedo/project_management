package ru.simbir.projectmanagement.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.simbir.projectmanagement.dto.request.UserUpdateRequest;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.model.User;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserResponse toResponse(User user);

    void updateUser(UserUpdateRequest userUpdateRequest, @MappingTarget User user);


    List<UserResponse> toList(Collection<User> users);
}

