package com.projeto.codearchive.identity.application.dto;

import java.util.UUID;

public record UpdateProfileVisibilityCommand(UUID userId, boolean isPublic) {}
