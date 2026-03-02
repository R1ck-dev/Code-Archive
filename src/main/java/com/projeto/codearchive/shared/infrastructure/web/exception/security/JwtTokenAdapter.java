package com.projeto.codearchive.shared.infrastructure.web.exception.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.projeto.codearchive.identity.application.port.TokenService;
import com.projeto.codearchive.identity.domain.model.User;

@Component
public class JwtTokenAdapter implements TokenService {
    
    @Value("${api.security.token.secret:default-secret-key-change-in-production}")
    private String secret;

    private static final String ISSUER = "code-archive-api";

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getId().toString())
                    .withExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS))
                    .sign(algorithm);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    @Override
    public String getSubjectFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    
}
