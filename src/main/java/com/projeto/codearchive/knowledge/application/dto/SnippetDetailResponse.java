package com.projeto.codearchive.knowledge.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SnippetDetailResponse(
    UUID id,
    String code,
    String description,
    String conceptCategory,
    OffsetDateTime createdAt
) {} 
