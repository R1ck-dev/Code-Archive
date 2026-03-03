package com.projeto.codearchive.knowledge.application.dto;

import java.util.List;
import java.util.UUID;

public record SubmitChallengeCommand(
    UUID authorId,
    String title,
    String platformOrigin,
    String sourceCode,
    String timeComplexity,
    String spaceComplexity,
    Integer aiAutonomyIndex,
    List<SnippetCommand> snippets
) {}
