package com.projeto.codearchive.knowledge.application.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projeto.codearchive.knowledge.application.dto.ChallengeDetailResponse;
import com.projeto.codearchive.knowledge.application.dto.SnippetDetailResponse;
import com.projeto.codearchive.knowledge.domain.model.Challenge;
import com.projeto.codearchive.knowledge.domain.repository.ChallengeRepository;

@Service
public class GetChallengeDetailUseCase {

    private final ChallengeRepository challengeRepository;

    public GetChallengeDetailUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public ChallengeDetailResponse execute(UUID challengeId, UUID requesterId) {
        
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found."));

        boolean isOwner = challenge.getAuthorId().equals(requesterId);
        boolean isPublic = challenge.isPublic();

        if (!isOwner && !isPublic) {
            throw new IllegalStateException("Access denied: You do not have permission to view this private challenge.");
        }

        List<SnippetDetailResponse> snippetResponses = challenge.getSnippets().stream()
                .map(snippet -> new SnippetDetailResponse(
                        snippet.getId(),
                        snippet.getCode(),
                        snippet.getDescription(),
                        snippet.getConceptCategory(),
                        snippet.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new ChallengeDetailResponse(
                challenge.getId(),
                challenge.getAuthorId(),
                challenge.getTitle(),
                challenge.getPlatformOrigin(),
                challenge.getSourceCode(),
                challenge.getTimeComplexity(),
                challenge.getSpaceComplexity(),
                challenge.getAiAutonomyIndex(),
                challenge.isPublic(),
                challenge.getCreatedAt(),
                challenge.getUpdatedAt(),
                snippetResponses
        );
    }
}
