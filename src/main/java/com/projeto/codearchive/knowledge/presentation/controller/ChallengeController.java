package com.projeto.codearchive.knowledge.presentation.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.projeto.codearchive.knowledge.application.dto.ChallengeDetailResponse;
import com.projeto.codearchive.knowledge.application.dto.ChallengeResponse;
import com.projeto.codearchive.knowledge.application.dto.ChallengeSummaryResponse;
import com.projeto.codearchive.knowledge.application.dto.SnippetCommand;
import com.projeto.codearchive.knowledge.application.dto.SubmitChallengeCommand;
import com.projeto.codearchive.knowledge.application.usecase.ChangeChallengeVisibilityUseCase;
import com.projeto.codearchive.knowledge.application.usecase.GetChallengeDetailUseCase;
import com.projeto.codearchive.knowledge.application.usecase.ListMyChallengesUseCase;
import com.projeto.codearchive.knowledge.application.usecase.ListPublicChallengesByAuthorUseCase;
import com.projeto.codearchive.knowledge.application.usecase.SubmitChallengeUseCase;
import com.projeto.codearchive.knowledge.presentation.dto.ChangeVisibilityRequest;
import com.projeto.codearchive.knowledge.presentation.dto.SubmitChallengeRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenges", description = "Gestão do portfólio de desafios de código e snippets")
public class ChallengeController {
    
    private final SubmitChallengeUseCase submitChallengeUseCase;
    private final ListMyChallengesUseCase listMyChallengesUseCase;
    private final ChangeChallengeVisibilityUseCase changeChallengeVisibilityUseCase;
    private final GetChallengeDetailUseCase getChallengeDetailUseCase;
    private final ListPublicChallengesByAuthorUseCase listPublicChallengesByAuthorUseCase;

    public ChallengeController(SubmitChallengeUseCase submitChallengeUseCase, ListMyChallengesUseCase listMyChallengesUseCase, ChangeChallengeVisibilityUseCase changeChallengeVisibilityUseCase, GetChallengeDetailUseCase getChallengeDetailUseCase,ListPublicChallengesByAuthorUseCase listPublicChallengesByAuthorUseCase) {
        this.submitChallengeUseCase = submitChallengeUseCase;
        this.listMyChallengesUseCase = listMyChallengesUseCase;
        this.changeChallengeVisibilityUseCase = changeChallengeVisibilityUseCase;
        this.getChallengeDetailUseCase = getChallengeDetailUseCase;
        this.listPublicChallengesByAuthorUseCase = listPublicChallengesByAuthorUseCase;
    }

    @Operation(summary = "Submeter um novo desafio", description = "Guarda um código-fonte e os seus respetivos snippets associados ao utilizador autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Desafio criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados submetidos inválidos ou incompletos")
    })
    @PostMapping
    public ResponseEntity<ChallengeResponse> submitChallenge(@Valid @RequestBody SubmitChallengeRequest request, Authentication authentication) {

        UUID authorId = UUID.fromString(authentication.getName());

        SubmitChallengeCommand command = mapToCommand(request, authorId);

        ChallengeResponse response = submitChallengeUseCase.execute(command);

        URI location =  ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    private SubmitChallengeCommand mapToCommand(SubmitChallengeRequest request, UUID authorId) {
        List<SnippetCommand> snippetCommands = request.snippets() == null ? List.of() : request.snippets().stream()
                        .map(s -> new SnippetCommand(s.code(), s.description(), s.conceptCategory()))
                        .collect(Collectors.toList());

        return new SubmitChallengeCommand(
                    authorId,
                    request.title(),
                    request.platformOrigin(),
                    request.sourceCode(),
                    request.timeComplexity(),
                    request.spaceComplexity(),
                    request.aiAutonomyIndex(),
                    snippetCommands
        );
    }

    @Operation(summary = "Listar os meus desafios", description = "Retorna uma lista resumida de todos os desafios (públicos e privados) do utilizador autenticado.")
    @ApiResponse(responseCode = "200", description = "Listagem recuperada com sucesso")
    @GetMapping
    private ResponseEntity<List<ChallengeSummaryResponse>> listMyChallenges(Authentication authentication) {
        UUID authorId = UUID.fromString(authentication.getName());

        List<ChallengeSummaryResponse> response = listMyChallengesUseCase.execute(authorId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar a visibilidade de um desafio", description = "Permite ao autor publicar ou ocultar um desafio do seu portfólio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado alterado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Apenas o autor pode alterar este recurso")
    })
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<Void> changeVisibility(@PathVariable UUID id, @Valid @RequestBody ChangeVisibilityRequest request, Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());

        changeChallengeVisibilityUseCase.execute(id, requesterId, request.isPublic());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obter detalhes de um desafio", description = "Recupera o código-fonte integral e os snippets de um desafio específico. Exige que o desafio seja público ou que o utilizador autenticado seja o autor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes recuperados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Desafio privado de outro autor"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetail(@PathVariable UUID id, Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());

        ChallengeDetailResponse response = getChallengeDetailUseCase.execute(id, requesterId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar portfólio público de um utilizador", description = "Retorna todos os desafios marcados como públicos de um autor específico.")
    @ApiResponse(responseCode = "200", description = "Listagem recuperada com sucesso")
    @GetMapping("/user/{authorId}")
    public ResponseEntity<List<ChallengeSummaryResponse>> listUserPublicPortfolio(@PathVariable UUID authorId) {

        List<ChallengeSummaryResponse> response = listPublicChallengesByAuthorUseCase.execute(authorId);

        return ResponseEntity.ok(response);
    }
}
