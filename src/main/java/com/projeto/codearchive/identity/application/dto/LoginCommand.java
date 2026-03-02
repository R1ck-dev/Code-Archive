package com.projeto.codearchive.identity.application.dto;

public record LoginCommand(
    String email,
    String rawPassword
) {} 
