package com.projeto.codearchive.knowledge.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChallengeSummaryResponse(
    UUID id,
    String title,
    String platformOrigin,
    String timeComplexity,
    String spaceComplexity,
    Integer aiAutonomyIndex,
    boolean isPublic,
    OffsetDateTime createdAt
) {}
