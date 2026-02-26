package com.projeto.codearchive.knowledge.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.projeto.codearchive.knowledge.domain.model.Challenge;

public interface ChallengeRepository {
    void save(Challenge challenge);
    Optional<Challenge> findById(UUID id);
    List<Challenge> findAllByAuthorId(UUID authorId);
    List<Challenge> findPublicChallengesByAuthorId(UUID authorId);
    void deleteById(UUID id);
} 
