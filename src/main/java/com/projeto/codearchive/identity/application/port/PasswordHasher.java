package com.projeto.codearchive.identity.application.port;

public interface PasswordHasher {
    String hash(String plainPassword);
    boolean matches(String plainPassword, String hashedPassword);
} 
