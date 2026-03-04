package com.projeto.codearchive.knowledge.application.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.knowledge.application.dto.ChallengeSummaryResponse;
import com.projeto.codearchive.knowledge.domain.model.Challenge;
import com.projeto.codearchive.knowledge.domain.repository.ChallengeRepository;

@Service
public class ListPublicChallengesByAuthorUseCase {
    
    private final ChallengeRepository challengeRepository;

    public ListPublicChallengesByAuthorUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public List<ChallengeSummaryResponse> execute(UUID authorId) {

        List<Challenge> publicChallenges = challengeRepository.findPublicChallengesByAuthorId(authorId);

        return publicChallenges.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    private ChallengeSummaryResponse toSummaryResponse(Challenge challenge) {
        return new ChallengeSummaryResponse(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getPlatformOrigin(),
                challenge.getTimeComplexity(),
                challenge.getSpaceComplexity(),
                challenge.getAiAutonomyIndex(),
                challenge.isPublic(),
                challenge.getCreatedAt()
        );
    }
}
