package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.request.RegistrationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;
import ru.simbir.projectmanagement.exception.OccupiedDataException;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.UserRepository;
import ru.simbir.projectmanagement.security.jwt.JwtTokenService;
import ru.simbir.projectmanagement.service.AuthenticationService;
import ru.simbir.projectmanagement.utils.enums.Role;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public TokenResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        return TokenResponse.builder()
                .accessToken(jwtTokenService.generateToken(userDetails))
                .build();
    }

    @Transactional
    @Override
    public UUID register(RegistrationRequest registrationRequest) {
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
