package com.projeto.codearchive.identity.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.projeto.codearchive.identity.domain.model.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existByUsername(String username);
    boolean existByEmail(String email);
} 
