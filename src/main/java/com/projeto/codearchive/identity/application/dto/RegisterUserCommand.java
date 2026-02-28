package com.projeto.codearchive.identity.application.dto;

public record RegisterUserCommand(
            String username,
            String email,
            String rawPassword
) {} 
