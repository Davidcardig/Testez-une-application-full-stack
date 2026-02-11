package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Tests unitaires")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("Devrait supprimer un utilisateur par son ID")
    void testDelete_Success() {
        // Given
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.delete(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Devrait supprimer un utilisateur avec un ID spécifique")
    void testDelete_WithDifferentId() {
        // Given
        Long userId = 999L;
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.delete(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Devrait retourner un utilisateur par son ID")
    void testFindById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.isAdmin()).isFalse();
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait retourner null quand l'utilisateur n'existe pas")
    void testFindById_NotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        User result = userService.findById(999L);

        // Then
        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(999L);
    }

}
