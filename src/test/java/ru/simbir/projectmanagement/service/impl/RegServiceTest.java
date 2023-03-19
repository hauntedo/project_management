package ru.simbir.projectmanagement.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.exception.OccupiedDataException;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.utils.TestUtils;
import ru.simbir.projectmanagement.utils.enums.Role;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegService is working")
class RegServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RegServiceImpl regService;

    @Nested
    @DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
    @DisplayName("register_user() is working")
    class registerUser {
        @Test
        void register_user() {
            //given
            User user = TestUtils.getUser();
            RegistrationRequest registrationRequest = RegistrationRequest.builder()
                    .email("test@test.test")
                    .password("qwerty")
                    .fullName("test")
                    .build();

            UUID expected = UUID.fromString(user.getId().toString());
            when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(Boolean.FALSE);
            when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
            when(userRepository.save(any())).thenReturn(user);

            //when
            UUID actual = regService.registerUser(registrationRequest);

            //then
            assertEquals(expected, actual);
        }

        @Test
        void throw_occupied_data_exception() {
            User user = TestUtils.getUser();
            when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(Boolean.TRUE);

            assertThrows(OccupiedDataException.class, () -> regService.registerUser(
                    RegistrationRequest.builder()
                            .email(user.getEmail())
                            .password(anyString())
                            .build()
            ));
        }
    }
}