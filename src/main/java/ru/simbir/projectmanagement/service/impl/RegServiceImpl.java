package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.exception.OccupiedDataException;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.service.RegService;
import ru.simbir.projectmanagement.utils.enums.Role;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegServiceImpl implements RegService {

    private static final Logger LOGGER = LogManager.getLogger(RegServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UUID registerUser(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        checkUserByEmail(email);
        User user = createUser(registrationRequest);
        user = saveUser(email, user);
        return user.getId();
    }

    private User saveUser(String email, User user) {
        LOGGER.info("#saveUser: try to save user by email {}", email);
        user = userRepository.save(user);
        LOGGER.info("#saveUser: user by email {} saved", email);
        return user;
    }

    private User createUser(RegistrationRequest registrationRequest) {
        return User.builder()
                .fullName(registrationRequest.getFullName())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .email(registrationRequest.getEmail())
                .role(Role.USER)
                .build();
    }

    private void checkUserByEmail(String email) {
        LOGGER.info("#checkUserByEmail: find user by email {}", email);
        if (userRepository.existsUserByEmail(email)) {
            LOGGER.warn("#checkUserByEmail: occupied user email {}. {}", email, OccupiedDataException.class.getSimpleName());
            throw new OccupiedDataException("Email is occupied " + email);
        }
    }
}
