package com.projeto.codearchive.knowledge.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Snippet {
    private final UUID id;
    private String code;
    private String description;
    private String conceptCategory;
    private final OffsetDateTime createdAt;

    public Snippet(UUID id, String code, String description, String conceptCategory, OffsetDateTime createdAt) {

        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Snippet code cannot be empty");

        this.id = id;
        this.code = code;
        this.description = description;
        this.conceptCategory = conceptCategory;
        this.createdAt = createdAt;
    }

    public void updateContent(String newCode, String newDescription) {
        if (newCode == null || newDescription == null)
            throw new IllegalArgumentException("Code cannot be empty");

        this.code = newCode;
        this.description = newDescription;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getConceptCategory() {
        return conceptCategory;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
