package com.projeto.codearchive.knowledge.application.usecase;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.knowledge.application.dto.ChallengeResponse;
import com.projeto.codearchive.knowledge.application.dto.SubmitChallengeCommand;
import com.projeto.codearchive.knowledge.domain.model.Challenge;
import com.projeto.codearchive.knowledge.domain.model.Snippet;
import com.projeto.codearchive.knowledge.domain.repository.ChallengeRepository;

import jakarta.transaction.Transactional;

@Service
public class SubmitChallengeUseCase {
    
    private final ChallengeRepository challengeRepository;

    public SubmitChallengeUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Transactional
    public ChallengeResponse execute(SubmitChallengeCommand command) {
        OffsetDateTime now = OffsetDateTime.now();
        UUID newChallengeId = UUID.randomUUID();

        Challenge challenge = new Challenge(
                    newChallengeId,
                    command.authorId(),
                    command.title(),
                    command.platformOrigin(),
                    command.sourceCode(),
                    now
        );

        challenge.updateResolutionMetrics(
                    command.timeComplexity(), 
                    command.spaceComplexity(), 
                    command.aiAutonomyIndex()
        );

        if (command.snippets() != null && !command.snippets().isEmpty()) {
            for (var snippetCmd : command.snippets()) {
                Snippet snippet = new Snippet(
                            UUID.randomUUID(),
                            snippetCmd.code(),
                            snippetCmd.description(),
                            snippetCmd.conceptCategory(),
                            now
                );

                challenge.addSnippet(snippet);
            }
        }

        challengeRepository.save(challenge);

        return new ChallengeResponse(
                    challenge.getId(),
                    challenge.getTitle(),
                    false,
                    now
        );
    }
}
