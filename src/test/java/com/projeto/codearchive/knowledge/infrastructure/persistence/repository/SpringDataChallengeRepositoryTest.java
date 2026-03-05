// package com.projeto.codearchive.knowledge.infrastructure.persistence.repository;

// import com.projeto.codearchive.knowledge.infrastructure.persistence.entity.ChallengeJpaEntity;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.containers.PostgreSQLContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;

// import java.time.OffsetDateTime;
// import java.util.List;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest // Impede que o Spring substitua o banco de dados configurado por um banco H2 em
//              // memória
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @Testcontainers
// class SpringDataChallengeRepositoryTest {

//         // Instancia um contêiner Docker do PostgreSQL na mesma versão utilizada em
//         // produção
//         @Container
//         static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
//                         .withDatabaseName("codearchive_test_db")
//                         .withUsername("test")
//                         .withPassword("test");

//         // Injeta dinamicamente as credenciais do contêiner no ApplicationContext do
//         // Spring
//         @DynamicPropertySource
//         static void configureProperties(DynamicPropertyRegistry registry) {
//                 registry.add("spring.datasource.url", postgres::getJdbcUrl);
//                 registry.add("spring.datasource.username", postgres::getUsername);
//                 registry.add("spring.datasource.password", postgres::getPassword);
//         }

//         @Autowired
//         private SpringDataChallengeRepository repository;

//         @Test
//         @DisplayName("Deve retornar apenas os desafios públicos de um autor específico")
//         void shouldReturnOnlyPublicChallengesByAuthor() {
//                 // 1. Arrange
//                 UUID authorId = UUID.randomUUID();
//                 UUID otherAuthorId = UUID.randomUUID();
//                 OffsetDateTime now = OffsetDateTime.now();

//                 // Desafio 1: Do autor alvo, Público (DEVE ser retornado)
//                 ChallengeJpaEntity publicChallenge = new ChallengeJpaEntity(
//                                 UUID.randomUUID(), authorId, "Public Title", "Origin", "Code", "O(1)", "O(1)",
//                                 (short) 1, true, now, now, List.of());

//                 // Desafio 2: Do autor alvo, Privado (NÃO deve ser retornado)
//                 ChallengeJpaEntity privateChallenge = new ChallengeJpaEntity(
//                                 UUID.randomUUID(), authorId, "Private Title", "Origin", "Code", "O(1)", "O(1)",
//                                 (short) 1, false, now, now, List.of());

//                 // Desafio 3: De outro autor, Público (NÃO deve ser retornado)
//                 ChallengeJpaEntity otherAuthorPublicChallenge = new ChallengeJpaEntity(
//                                 UUID.randomUUID(), otherAuthorId, "Other Title", "Origin", "Code", "O(1)", "O(1)",
//                                 (short) 1, true, now, now, List.of());

//                 repository.saveAll(List.of(publicChallenge, privateChallenge, otherAuthorPublicChallenge));

//                 // 2. Act
//                 List<ChallengeJpaEntity> result = repository.findAllByAuthorIdAndIsPublicTrue(authorId);

//                 // 3. Assert
//                 assertNotNull(result);
//                 assertEquals(1, result.size(), "Apenas 1 desafio deve satisfazer ambos os critérios");
//                 assertEquals("Public Title", result.get(0).getTitle());
//                 assertTrue(result.get(0).isPublic());
//                 assertEquals(authorId, result.get(0).getAuthorId());
//         }
// }