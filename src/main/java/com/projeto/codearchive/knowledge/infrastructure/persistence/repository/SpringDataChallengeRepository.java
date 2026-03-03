package com.projeto.codearchive.knowledge.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.codearchive.knowledge.infrastructure.persistence.entity.ChallengeJpaEntity;

@Repository
public interface SpringDataChallengeRepository extends JpaRepository<ChallengeJpaEntity, UUID> {
    List<ChallengeJpaEntity> findAllByAuthorId(UUID authorId);
    List<ChallengeJpaEntity> findAllByAuthorIdAndIsPublicTrue(UUID authorId);
}
