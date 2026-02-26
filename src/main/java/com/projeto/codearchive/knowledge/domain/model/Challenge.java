package com.projeto.codearchive.knowledge.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Challenge {
    private final UUID id;
    private final UUID authorId;
    private String title;
    private String platformOrigin;
    private String sourceCode;
    private String timeComplexity;
    private String spaceComplexity;
    private Integer aiAutonomyIndex;
    private boolean isPublic;
    private final List<Snippet> snippets;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Challenge(UUID id, UUID authorId, String title, String platformOrigin, String sourceCode,
            OffsetDateTime createdAt) {

        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title is required");
        if (authorId == null)
            throw new IllegalArgumentException("Author ID is required");

        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.platformOrigin = platformOrigin;
        this.sourceCode = sourceCode;
        this.isPublic = false;
        this.snippets = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void addSnippet(Snippet snippet) {
        if (snippet == null)
            throw new IllegalArgumentException("Snippet cannot be null");
        this.snippets.add(snippet);
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateResolutionMetrics(String timeComplexity, String spaceComplexity, Integer aiAutonomyIndex) {
        if (aiAutonomyIndex != null && (aiAutonomyIndex < 1 || aiAutonomyIndex > 5)) {
            throw new IllegalArgumentException("AI Autonomy Index must be between 1 and 5");
        }
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.aiAutonomyIndex = aiAutonomyIndex;
        this.updatedAt = OffsetDateTime.now();
    }

    public void publish() {
        this.isPublic = true;
        this.updatedAt = OffsetDateTime.now();
    }

    // Retorna uma lista imutável para proteger o encapsulamento da coleção
    public List<Snippet> getSnippets() {
        return Collections.unmodifiableList(snippets);
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getPlatformOrigin() {
        return platformOrigin;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public Integer getAiAutonomyIndex() {
        return aiAutonomyIndex;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}
