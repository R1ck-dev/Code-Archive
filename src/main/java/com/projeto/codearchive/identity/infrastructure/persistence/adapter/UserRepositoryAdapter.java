package com.projeto.codearchive.identity.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.projeto.codearchive.identity.domain.model.User;
import com.projeto.codearchive.identity.domain.repository.UserRepository;
import com.projeto.codearchive.identity.infrastructure.persistence.entity.UserJpaEntity;
import com.projeto.codearchive.identity.infrastructure.persistence.entity.repository.SpringDataUserRepository;

@Component
public class UserRepositoryAdapter implements UserRepository {
    
    private final SpringDataUserRepository springDataUserRepository;

    public UserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public void save(User user) {
        UserJpaEntity entity = toJpaEntity(user);
        springDataUserRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByUsername(username).map(this::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email).map(this::toDomainEntity);
    }

    @Override
    public boolean existByUsername(String username) {
        return springDataUserRepository.existsByEmail(username);
    }

    @Override
    public boolean existByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    // Métodos de Mapeamento Privados
    private UserJpaEntity toJpaEntity(User domain) {
        return new UserJpaEntity(
                    domain.getId(),
                    domain.getUsername(),
                    domain.getEmail(),
                    domain.getPasswordHash(),
                    domain.getIsProfilePublic(),
                    domain.getCreatedAt(),
                    domain.getUpdatedAt()
        );
    }

    private User toDomainEntity(UserJpaEntity entity) {
        return new User(
                    entity.getId(),
                    entity.getUsername(),
                    entity.getEmail(),
                    entity.getPasswordHash(),
                    entity.isProfilePublic(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
        );
    }
}
