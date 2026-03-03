package com.projeto.codearchive.knowledge.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChallengeResponse(
    UUID id,
    String title,
    boolean isPublic,
    OffsetDateTime createdAt
) {}
