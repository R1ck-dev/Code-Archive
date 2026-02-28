package com.projeto.codearchive.identity.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.projeto.codearchive.identity.application.dto.RegisterUserCommand;
import com.projeto.codearchive.identity.application.dto.UserProfileResponse;
import com.projeto.codearchive.identity.application.usecase.RegisterUserUseCase;
import com.projeto.codearchive.identity.presentation.dto.RegisterUserRequest;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final RegisterUserUseCase registerUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        // Mapeamento: Web Request -> Application Command
        RegisterUserCommand command = new RegisterUserCommand(
                    request.username(),
                    request.email(),
                    request.password()
        );

        // Invocando caso de uso
        UserProfileResponse response = registerUserUseCase.execute(command);

        // Construção da URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        // Retorno HTTP 201
        return ResponseEntity.created(location).body(response);
    }
    
}
