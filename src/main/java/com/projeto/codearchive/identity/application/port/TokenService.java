package com.projeto.codearchive.identity.application.port;

import com.projeto.codearchive.identity.domain.model.User;

public interface TokenService {
    String generateToken(User user);
    String getSubjectFromToken(String token);
} 
