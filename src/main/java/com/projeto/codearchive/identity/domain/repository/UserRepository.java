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

/* No projeto o repositório é dividido em duas partes, Interface no Domínio e Classe na Infraestrutura.
Interface no Domínio: Define o que precisa ser feito utilizando apenas Java puro e as Entidades de Domínio
Classe na Infraestrutura: Define como será feito, utilizando o Sprin Data JPA para implementar o contrato definido pelo domínio*/

/*As interfaces no domínio não dependem de frameworks. Assim se o projeto exigir migração do armazenamento para um banco NoSQL para lidar melhor com grandes volumes de código, apenas a camada de infraestrutura será reescrita. O domínio e os Casos de Uso permanecerão intactos, pois dependem apenas destas interfaces */

