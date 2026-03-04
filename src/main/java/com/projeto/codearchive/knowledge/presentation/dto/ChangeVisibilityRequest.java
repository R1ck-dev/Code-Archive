package com.projeto.codearchive.knowledge.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeVisibilityRequest(
    @NotNull(message = "Visibility state must be provided")
    Boolean isPublic
) {} 
