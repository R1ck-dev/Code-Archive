package com.projeto.codearchive.identity.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserProfileResponse(
            UUID id,
            String username,
            boolean isPublic,
            OffsetDateTime createdAt
) {}
