package com.projeto.codearchive.knowledge.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.knowledge.domain.model.Challenge;
import com.projeto.codearchive.knowledge.domain.repository.ChallengeRepository;

@Service
public class ChangeChallengeVisibilityUseCase {
    
    private final ChallengeRepository challengeRepository;

    public ChangeChallengeVisibilityUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public void execute(UUID challengeId, UUID requesterId, boolean makePublic) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge nor found."));

        if (!challenge.getAuthorId().equals(requesterId)) {
            throw new IllegalStateException("User is not authorized to modify this resource");
        }

        if (makePublic) {
            challenge.publish();
        } else {
            challenge.unpublish();
        }

        challengeRepository.save(challenge);
    }
}
