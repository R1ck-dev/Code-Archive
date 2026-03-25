package com.projeto.codearchive.identity.application.usecase;

// import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.identity.application.dto.UpdateProfileVisibilityCommand;
import com.projeto.codearchive.identity.domain.model.User;
import com.projeto.codearchive.identity.domain.repository.UserRepository;

@Service
public class UpdateProfileVisibilityUseCase {

    private final UserRepository userRepository;

    public UpdateProfileVisibilityUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(UpdateProfileVisibilityCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (command.isPublic()) {
            user.makeProfilePublic();
        } else {
            user.makeProfilePrivate();
        }
        userRepository.save(user);
    }
}
