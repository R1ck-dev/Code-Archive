// package com.projeto.codearchive.knowledge.presentation.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.projeto.codearchive.knowledge.application.dto.ChallengeResponse;
// import com.projeto.codearchive.knowledge.application.dto.SubmitChallengeCommand;
// import com.projeto.codearchive.knowledge.application.usecase.ChangeChallengeVisibilityUseCase;
// import com.projeto.codearchive.knowledge.application.usecase.GetChallengeDetailUseCase;
// import com.projeto.codearchive.knowledge.application.usecase.ListMyChallengesUseCase;
// import com.projeto.codearchive.knowledge.application.usecase.ListPublicChallengesByAuthorUseCase;
// import com.projeto.codearchive.knowledge.application.usecase.SubmitChallengeUseCase;
// import com.projeto.codearchive.knowledge.presentation.dto.SubmitChallengeRequest;
// import com.projeto.codearchive.shared.infrastructure.security.JwtAuthenticationFilter;
// import com.projeto.codearchive.shared.infrastructure.security.SecurityConfig;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.OffsetDateTime;
// import java.util.List;
// import java.util.UUID;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(controllers = ChallengeController.class)
// @AutoConfigureMockMvc(addFilters = false) // Desativa filtros reais de servlet para focar no roteamento e validação do Controller
// class ChallengeControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // @MockBean substitui os Casos de Uso reais por Mocks no contexto do Spring
//     @MockBean
//     private SubmitChallengeUseCase submitChallengeUseCase;

//     @MockBean
//     private ListMyChallengesUseCase listMyChallengesUseCase;

//     @MockBean
//     private ChangeChallengeVisibilityUseCase changeChallengeVisibilityUseCase;

//     @MockBean
//     private GetChallengeDetailUseCase getChallengeDetailUseCase;

//     @MockBean
//     private ListPublicChallengesByAuthorUseCase listPublicChallengesByAuthorUseCase;

//     @Test
//     @DisplayName("Deve retornar HTTP 201 Created quando o payload for válido")
//     // Injeta um Authentication falso na Thread. O "username" simula o ID do autor extraído pelo filtro JWT.
//     @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
//     void shouldReturn201WhenRequestIsValid() throws Exception {
//         // 1. Arrange
//         SubmitChallengeRequest request = new SubmitChallengeRequest(
//                 "Valid Title", "LeetCode", "System.out.println();",
//                 "O(1)", "O(1)", 1, List.of()
//         );

//         UUID expectedChallengeId = UUID.randomUUID();
//         ChallengeResponse mockResponse = new ChallengeResponse(
//                 expectedChallengeId, "Valid Title", false, OffsetDateTime.now()
//         );

//         when(submitChallengeUseCase.execute(any(SubmitChallengeCommand.class))).thenReturn(mockResponse);

//         // 2. Act & Assert
//         mockMvc.perform(post("/api/v1/challenges")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isCreated())
//                 .andExpect(header().exists("Location")) // Valida conformidade REST (HATEOAS)
//                 .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/v1/challenges/" + expectedChallengeId)))
//                 .andExpect(jsonPath("$.id").value(expectedChallengeId.toString()))
//                 .andExpect(jsonPath("$.title").value("Valid Title"));
//     }

//     @Test
//     @DisplayName("Deve retornar HTTP 400 Bad Request quando campos obrigatórios estiverem ausentes")
//     @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
//     void shouldReturn400WhenRequestIsInvalid() throws Exception {
//         // 1. Arrange (Payload com título e código em branco, violando @NotBlank)
//         SubmitChallengeRequest request = new SubmitChallengeRequest(
//                 "", "LeetCode", "", "O(1)", "O(1)", 6, List.of()
//         );

//         // 2. Act & Assert
//         // O Caso de Uso não precisa ser mockado porque a execução será abortada na camada de borda (Controller)
//         mockMvc.perform(post("/api/v1/challenges")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isBadRequest())
//                 // Opcional: validar a presença do formato ProblemDetail (se o @RestControllerAdvice estiver mapeado no WebMvcTest)
//                 .andExpect(jsonPath("$.title").exists()); 
//     }
// }