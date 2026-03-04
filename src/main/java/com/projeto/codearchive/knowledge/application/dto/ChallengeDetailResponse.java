package com.projeto.codearchive.knowledge.application.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ChallengeDetailResponse(
    UUID id,
    UUID authorId,
    String title,
    String platformOrigin,
    String sourceCode,
    String timeComplexity,
    String spaceComplexity,
    Integer aiAutonomyIndex,
    boolean isPublic,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    List<SnippetDetailResponse> snippets
) {} 
