package ru.simbir.projectmanagement.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.simbir.projectmanagement.dto.response.UserResponse;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.utils.enums.Role;
import ru.simbir.projectmanagement.utils.mapper.UserMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService is working")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.test")
                .password("qwerty")
                .fullName("test")
                .role(Role.USER)
                .id(UUID.fromString("adadd510-c505-11ed-afa1-0242ac120002"))
                .build();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("getUserById is working")
    class getUserById {

        @Test
        void get_user_by_id() {
            UserResponse userResponse = UserResponse.builder()
                    .email("test@test.test")
                    .fullName("test")
                    .role(Role.USER.name())
                    .id(UUID.fromString("adadd510-c505-11ed-afa1-0242ac120002"))
                    .build();
            Optional<User> optionalUser = Optional.of(user);
            when(userRepository.findById(any())).thenReturn(optionalUser);
            when(userMapper.toResponse(optionalUser.get())).thenReturn(userResponse);
            UserResponse expected = UserResponse.builder()
                    .email("test@test.test")
                    .fullName("test")
                    .role(Role.USER.name())
                    .id(UUID.fromString("adadd510-c505-11ed-afa1-0242ac120002"))
                    .build();

            UserResponse actual = userService.getUserById(UUID.fromString("adadd510-c505-11ed-afa1-0242ac120002"));

            assertEquals(expected, actual);
        }

    }

}