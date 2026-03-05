package com.projeto.codearchive.identity.application.usecase;

import com.projeto.codearchive.identity.application.dto.RegisterUserCommand;
import com.projeto.codearchive.identity.application.dto.UserProfileResponse;
import com.projeto.codearchive.identity.application.port.PasswordHasher;
import com.projeto.codearchive.identity.domain.model.User;
import com.projeto.codearchive.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Anotação necessária para inicializar os Mocks do Mockito
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    // @Mock cria instâncias falsas das dependências. Nenhuma conexão real é feita.
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    // @InjectMocks injeta os Mocks acima na classe que queremos testar
    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Deve registrar um usuário com sucesso quando os dados forem válidos")
    void shouldRegisterUserSuccessfully() {
        // 1. Arrange (Preparar)
        RegisterUserCommand command = new RegisterUserCommand("aturing", "alan@mail.com", "rawPass123");
        
        // Configurando o comportamento dos mocks
        when(userRepository.existByEmail("alan@mail.com")).thenReturn(false);
        when(userRepository.existByUsername("aturing")).thenReturn(false);
        when(passwordHasher.hash("rawPass123")).thenReturn("hashedPassXYZ");

        // 2. Act (Agir)
        UserProfileResponse response = registerUserUseCase.execute(command);

        // 3. Assert (Verificar)
        assertNotNull(response);
        assertEquals("aturing", response.username());
        assertNotNull(response.id());
        
        // Verifica se o método save() foi chamado exatamente uma vez com qualquer objeto User
        verify(userRepository, times(1)).save(any(User.class));

        // Capturar o objeto exato que foi passado para o save() para inspeção interna
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertEquals("hashedPassXYZ", savedUser.getPasswordHash());
        assertFalse(savedUser.getIsProfilePublic());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email já estiver registrado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // 1. Arrange
        RegisterUserCommand command = new RegisterUserCommand("aturing", "alan@mail.com", "rawPass123");
        
        // Simulando que o email já existe na base
        when(userRepository.existByEmail("alan@mail.com")).thenReturn(true);

        // 2. Act & 3. Assert
        // Verifica se a execução lança a exceção esperada
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            registerUserUseCase.execute(command);
        });

        // Verifica a mensagem da exceção
        assertEquals("Email already registered.", exception.getMessage());
        
        // Verifica que o hash de senha e o save NUNCA foram chamados, garantindo o fail-fast
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any());
    }
}