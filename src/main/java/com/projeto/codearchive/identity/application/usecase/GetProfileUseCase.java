package com.projeto.codearchive.identity.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.identity.application.dto.UserProfileResponse;
import com.projeto.codearchive.identity.domain.model.User;
import com.projeto.codearchive.identity.domain.repository.UserRepository;

@Service
public class GetProfileUseCase {

    private final UserRepository userRepository;

    public GetProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse execute(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                Boolean.TRUE.equals(user.getIsProfilePublic()),
                user.getCreatedAt()
        );
    }
}
