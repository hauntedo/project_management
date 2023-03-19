package ru.simbir.projectmanagement.security.jwt.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.simbir.projectmanagement.security.jwt.JwtTokenService;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private final Duration jwtTokenValidityTime;

    private final Algorithm hmac512;
    private final JWTVerifier jwtVerifier;

    public JwtTokenServiceImpl(@Value("${jwt.secret}") final String secret, @Value("${jwt.expiration.time}") final long time) {
        this.jwtTokenValidityTime = Duration.ofSeconds(time);
        this.hmac512 = Algorithm.HMAC512(secret);
        this.jwtVerifier = JWT.require(this.hmac512).build();
    }

    @Override
    public String generateToken(UserDetails userDetails) {

        Instant now = Instant.now();
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(jwtTokenValidityTime.toMillis()))
                .withIssuer("app")
                .sign(this.hmac512);
    }

    @Override
    public String validateToken(String token) {
        try {
            return jwtVerifier.verify(token).getSubject();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Token expired");
        }
    }
}
