package com.projeto.codearchive.knowledge.presentation.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SubmitChallengeRequest(
    @NotBlank(message = "Title is required")
    String title,

    String platformOrigin,

    @NotBlank(message = "Source code is required")
    String sourceCode,

    String timeComplexity,
    String spaceComplexity,

    @Min(1) @Max(5)
    Integer aiAutonomyIndex,

    @Valid
    List<SnippetRequest> snippets
) {
    public record SnippetRequest(
        @NotBlank(message = "Snippet code is required")
        String code,
        String description,
        String conceptCategory
    ) {}
} 
