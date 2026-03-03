package com.projeto.codearchive.knowledge.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.projeto.codearchive.knowledge.domain.model.Challenge;
import com.projeto.codearchive.knowledge.domain.model.Snippet;
import com.projeto.codearchive.knowledge.domain.repository.ChallengeRepository;
import com.projeto.codearchive.knowledge.infrastructure.persistence.entity.ChallengeJpaEntity;
import com.projeto.codearchive.knowledge.infrastructure.persistence.entity.SnippetJpaEntity;
import com.projeto.codearchive.knowledge.infrastructure.persistence.repository.SpringDataChallengeRepository;

@Component
public class ChallengeRepositoryAdapter implements ChallengeRepository {

    private final SpringDataChallengeRepository springDataRepository;

    public ChallengeRepositoryAdapter(SpringDataChallengeRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public void save(Challenge challenge) {
        ChallengeJpaEntity entity = toJpaEntity(challenge);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<Challenge> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public List<Challenge> findAllByAuthorId(UUID authorId) {
        return springDataRepository.findAllByAuthorId(authorId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Challenge> findPublicChallengesByAuthorId(UUID authorId) {
        return springDataRepository.findAllByAuthorIdAndIsPublicTrue(authorId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }

    // Mapeamento de Dominio <-> JPA

    private ChallengeJpaEntity toJpaEntity(Challenge domain) {

        Short aiAutonomyIndex = domain.getAiAutonomyIndex() != null ? domain.getAiAutonomyIndex().shortValue() : null;

        ChallengeJpaEntity entity = new ChallengeJpaEntity(
                domain.getId(),
                domain.getAuthorId(),
                domain.getTitle(),
                domain.getPlatformOrigin(),
                domain.getSourceCode(),
                domain.getTimeComplexity(),
                domain.getSpaceComplexity(),
                aiAutonomyIndex,
                domain.isPublic(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                new java.util.ArrayList<>());

        if (domain.getSnippets() != null) {
            domain.getSnippets().forEach(domainSnippet -> {
                SnippetJpaEntity snippetEntity = new SnippetJpaEntity(
                        domain.getId(),
                        entity,
                        domainSnippet.getCode(),
                        domainSnippet.getDescription(),
                        domainSnippet.getConceptCategory(),
                        domainSnippet.getCreatedAt());
                entity.addSnippet(snippetEntity);
            });
        }
        return entity;
    }

    private Challenge toDomainEntity(ChallengeJpaEntity entity) {
        Challenge domain = new Challenge(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getPlatformOrigin(),
                entity.getSourceCode(),
                entity.getCreatedAt());

        Integer aiAutonomyIndex = entity.getAiAutonomyIndex() != null ? entity.getAiAutonomyIndex().intValue() : null;

        domain.updateResolutionMetrics(entity.getTimeComplexity(), entity.getSpaceComplexity(),
                aiAutonomyIndex);

        if (entity.isPublic()) {
            domain.publish();
        }

        entity.getSnippets().forEach(snippetEntity -> {
            Snippet snippetDomain = new Snippet(
                    snippetEntity.getId(),
                    snippetEntity.getCode(),
                    snippetEntity.getDescription(),
                    snippetEntity.getConceptCategory(),
                    snippetEntity.getCreatedAt());
            domain.addSnippet(snippetDomain);
        });
        return domain;
    }
}
