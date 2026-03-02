package com.projeto.codearchive.identity.application.usecase;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.identity.application.dto.LoginCommand;
import com.projeto.codearchive.identity.application.dto.TokenResponse;
import com.projeto.codearchive.identity.application.port.PasswordHasher;
import com.projeto.codearchive.identity.application.port.TokenService;
import com.projeto.codearchive.identity.domain.model.User;
import com.projeto.codearchive.identity.domain.repository.UserRepository;

@Service
public class AuthenticateUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public AuthenticateUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public TokenResponse execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordHasher.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = tokenService.generateToken(user);
        return new TokenResponse(token);
    }
}
