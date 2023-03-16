package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
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

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public UUID registerUser(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        String password = registrationRequest.getPassword();
        if (userRepository.existsUserByEmail(email)) {
            throw new OccupiedDataException("Email is occupied " + email);
        }
        User user = User.builder()
                .fullName(registrationRequest.getFullName())
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        user.setRole(Role.USER);
        userRepository.save(user);
        return user.getId();
    }
}