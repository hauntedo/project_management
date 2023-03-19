package ru.simbir.projectmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;
import ru.simbir.projectmanagement.security.jwt.JwtTokenService;
import ru.simbir.projectmanagement.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;

    @Transactional(readOnly = true)
    @Override
    public TokenResponse authenticate(AuthenticationRequest authenticationRequest) {
        String email = authenticationRequest.getEmail();
        checkAuthentication(authenticationRequest, email);
        UserDetails userDetails = getUserDetails(email);
        return TokenResponse.builder()
                .accessToken(jwtTokenService.generateToken(userDetails))
                .build();
    }

    private UserDetails getUserDetails(String email) {
        LOGGER.info("#authenticate: find user by email: {}", email);
        return userDetailsService.loadUserByUsername(email);
    }

    private void checkAuthentication(AuthenticationRequest authenticationRequest, String email) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,
                    authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
