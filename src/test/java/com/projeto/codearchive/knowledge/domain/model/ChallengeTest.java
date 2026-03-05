package com.projeto.codearchive.knowledge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    @Test
    @DisplayName("Deve instanciar um Challenge válido e inicializar com estado privado")
    void shouldCreateValidChallenge() {
        // Arrange & Act
        UUID challengeId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        Challenge challenge = new Challenge(
                challengeId, authorId, "Two Sum", "LeetCode", "code {}", now
        );

        // Assert
        assertNotNull(challenge);
        assertEquals(challengeId, challenge.getId());
        assertEquals("Two Sum", challenge.getTitle());
        assertFalse(challenge.isPublic(), "O desafio deve ser criado como privado por padrão");
        assertTrue(challenge.getSnippets().isEmpty(), "A lista de snippets deve inicializar vazia");
    }

    @Test
    @DisplayName("Não deve permitir a instanciação de um Challenge com título vazio")
    void shouldThrowExceptionWhenTitleIsBlank() {
        // Arrange
        UUID challengeId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Challenge(challengeId, authorId, "   ", "LeetCode", "code {}", now);
        });

        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar as métricas de resolução com valores válidos")
    void shouldUpdateResolutionMetricsSuccessfully() {
        // Arrange
        Challenge challenge = new Challenge(
                UUID.randomUUID(), UUID.randomUUID(), "Title", "Origin", "Code", OffsetDateTime.now()
        );

        // Act
        challenge.updateResolutionMetrics("O(N)", "O(1)", 3);

        // Assert
        assertEquals(3, challenge.getAiAutonomyIndex());
        // Considerando que foram adicionados getters para as métricas na entidade:
        assertEquals("O(N)", challenge.getTimeComplexity());
        assertEquals("O(1)", challenge.getSpaceComplexity());
    }

    @Test
    @DisplayName("Não deve permitir Índice de IA fora do intervalo 1 a 5")
    void shouldThrowExceptionWhenAiIndexIsInvalid() {
        // Arrange
        Challenge challenge = new Challenge(
                UUID.randomUUID(), UUID.randomUUID(), "Title", "Origin", "Code", OffsetDateTime.now()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            challenge.updateResolutionMetrics("O(N)", "O(N)", 6); // Valor inválido
        });

        assertEquals("AI Autonomy Index must be between 1 and 5", exception.getMessage());
    }

    @Test
    @DisplayName("Deve garantir o encapsulamento da coleção de snippets (Imutabilidade externa)")
    void shouldProtectSnippetCollectionEncapsulation() {
        // Arrange
        Challenge challenge = new Challenge(
                UUID.randomUUID(), UUID.randomUUID(), "Title", "Origin", "Code", OffsetDateTime.now()
        );
        Snippet snippet = new Snippet(UUID.randomUUID(), "code", "desc", "cat", OffsetDateTime.now());
        
        // Act - Adicionando da forma correta
        challenge.addSnippet(snippet);

        // Assert - Tentando burlar o encapsulamento (deve lançar UnsupportedOperationException)
        assertThrows(UnsupportedOperationException.class, () -> {
            challenge.getSnippets().add(snippet); // Tenta adicionar diretamente na lista retornada
        });
        
        // A lista deve conter apenas 1 elemento inserido através do método addSnippet
        assertEquals(1, challenge.getSnippets().size());
    }
}