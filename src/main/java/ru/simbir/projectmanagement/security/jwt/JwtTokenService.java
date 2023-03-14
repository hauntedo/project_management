package ru.simbir.projectmanagement.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenService {

    String generateToken(UserDetails userDetails);

    String validateToken(String token);
}
