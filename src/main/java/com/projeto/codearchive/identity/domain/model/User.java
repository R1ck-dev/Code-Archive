package com.projeto.codearchive.identity.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private Boolean isProfilePublic;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User(UUID id, String username, String email, String passwordHash, Boolean isProfilePublic,
            OffsetDateTime createdAt, OffsetDateTime updatedAt) {

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email format");

        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isProfilePublic = isProfilePublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    // Ações de negócio
    public void makeProfilePublic() {
        this.isProfilePublic = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void makeProfilePrivate() {
        this.isProfilePublic = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Boolean getIsProfilePublic() {
        return isProfilePublic;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}
