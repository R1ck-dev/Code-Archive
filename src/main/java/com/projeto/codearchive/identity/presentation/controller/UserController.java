package com.projeto.codearchive.identity.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.projeto.codearchive.identity.application.dto.RegisterUserCommand;
import com.projeto.codearchive.identity.application.dto.UpdateProfileVisibilityCommand;
import com.projeto.codearchive.identity.application.dto.UserProfileResponse;
import com.projeto.codearchive.identity.application.usecase.GetProfileUseCase;
import com.projeto.codearchive.identity.application.usecase.RegisterUserUseCase;
import com.projeto.codearchive.identity.application.usecase.UpdateProfileVisibilityUseCase;
import com.projeto.codearchive.identity.presentation.dto.RegisterUserRequest;
import com.projeto.codearchive.identity.presentation.dto.UpdateProfileVisibilityRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Gestão de registo e perfis de utilizadores do Code Archive")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileVisibilityUseCase updateProfileVisibilityUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          GetProfileUseCase getProfileUseCase,
                          UpdateProfileVisibilityUseCase updateProfileVisibilityUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileVisibilityUseCase = updateProfileVisibilityUseCase;
    }

    @Operation(summary = "Registar um novo utilizador", description = "Cria uma nova conta na plataforma e devolve os dados básicos do perfil.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilizador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha na validação dos dados de entrada"),
            @ApiResponse(responseCode = "409", description = "Conflito: Email ou Username já encontram-se registados")
    })
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

    @Operation(summary = "Obter perfil do utilizador autenticado", description = "Devolve o ID, username e visibilidade do perfil do utilizador atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UserProfileResponse response = getProfileUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar visibilidade do perfil", description = "Torna o perfil público ou privado. Quando público, outros podem ver os seus desafios públicos através do ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visibilidade atualizada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyProfileVisibility(
            @Valid @RequestBody UpdateProfileVisibilityRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        updateProfileVisibilityUseCase.execute(new UpdateProfileVisibilityCommand(userId, request.isPublic()));
        return ResponseEntity.noContent().build();
    }
}
