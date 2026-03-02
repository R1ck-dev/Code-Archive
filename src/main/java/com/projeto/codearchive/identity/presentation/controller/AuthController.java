package com.projeto.codearchive.identity.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.codearchive.identity.application.dto.LoginCommand;
import com.projeto.codearchive.identity.application.dto.TokenResponse;
import com.projeto.codearchive.identity.application.usecase.AuthenticateUserUseCase;
import com.projeto.codearchive.identity.presentation.dto.LoginRequest;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        TokenResponse response = authenticateUserUseCase.execute(command);
        return ResponseEntity.ok(response);
    }
    
}
