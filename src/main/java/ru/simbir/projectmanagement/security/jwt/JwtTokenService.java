package ru.simbir.projectmanagement.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import ru.simbir.projectmanagement.dto.response.TokenResponse;
import ru.simbir.projectmanagement.dto.response.UserResponse;

public interface JwtTokenService {

    String generateToken(UserDetails userDetails);
    String validateToken(String token);
}
