package com.projeto.codearchive.knowledge.application.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.knowledge.application.dto.ChallengeSummaryResponse;
import com.projeto.codearchive.knowledge.domain.model.Challenge;
import com.projeto.codearchive.knowledge.domain.repository.ChallengeRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ListMyChallengesUseCase {
    
    private final ChallengeRepository challengeRepository;

    public ListMyChallengesUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Transactional(readOnly = true)
    public List<ChallengeSummaryResponse> execute(UUID authorId) {
        List<Challenge> challenges = challengeRepository.findAllByAuthorId(authorId);

        return challenges.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public ChallengeSummaryResponse toSummaryResponse(Challenge challenge) {
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
