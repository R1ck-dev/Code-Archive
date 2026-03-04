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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/challenges")
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

    @GetMapping
    private ResponseEntity<List<ChallengeSummaryResponse>> listMyChallenges(Authentication authentication) {
        UUID authorId = UUID.fromString(authentication.getName());

        List<ChallengeSummaryResponse> response = listMyChallengesUseCase.execute(authorId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<Void> changeVisibility(@PathVariable UUID id, @Valid @RequestBody ChangeVisibilityRequest request, Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());

        changeChallengeVisibilityUseCase.execute(id, requesterId, request.isPublic());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetail(@PathVariable UUID id, Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());

        ChallengeDetailResponse response = getChallengeDetailUseCase.execute(id, requesterId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{authorId}")
    public ResponseEntity<List<ChallengeSummaryResponse>> listUserPublicPortfolio(@PathVariable UUID authorId) {

        List<ChallengeSummaryResponse> response = listPublicChallengesByAuthorUseCase.execute(authorId);

        return ResponseEntity.ok(response);
    }
}
