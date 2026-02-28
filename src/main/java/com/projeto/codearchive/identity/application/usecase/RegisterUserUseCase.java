package com.projeto.codearchive.identity.application.usecase;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.identity.application.dto.RegisterUserCommand;
import com.projeto.codearchive.identity.application.dto.UserProfileResponse;
import com.projeto.codearchive.identity.application.port.PasswordHasher;
import com.projeto.codearchive.identity.domain.model.User;
import com.projeto.codearchive.identity.domain.repository.UserRepository;

@Service
public class RegisterUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserProfileResponse execute(RegisterUserCommand command) {
        // Validação de Unicidade
        if (userRepository.existByEmail(command.email())) {
            throw new IllegalStateException("Email already registered.");
        }
        if (userRepository.existByUsername(command.username())) {
            throw new IllegalStateException("Username already taken.");
        }

        // Criptografia
        String hashedPassword = passwordHasher.hash(command.rawPassword());
        UUID newUserid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User newUser = new User(
                    newUserid,
                    command.username(),
                    command.email(),
                    hashedPassword,
                    false,
                    now,
                    now
        );

        userRepository.save(newUser);

        return new UserProfileResponse(
                    newUser.getId(),
                    newUser.getUsername(),
                    newUser.getIsProfilePublic(),
                    newUser.getCreatedAt()
        );
    }
}
