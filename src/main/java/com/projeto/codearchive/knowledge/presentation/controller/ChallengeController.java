package com.projeto.codearchive.knowledge.presentation.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.projeto.codearchive.knowledge.application.dto.ChallengeResponse;
import com.projeto.codearchive.knowledge.application.dto.SnippetCommand;
import com.projeto.codearchive.knowledge.application.dto.SubmitChallengeCommand;
import com.projeto.codearchive.knowledge.application.usecase.SubmitChallengeUseCase;
import com.projeto.codearchive.knowledge.presentation.dto.SubmitChallengeRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {
    
    private final SubmitChallengeUseCase submitChallengeUseCase;

    public ChallengeController(SubmitChallengeUseCase submitChallengeUseCase) {
        this.submitChallengeUseCase = submitChallengeUseCase;
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
}
