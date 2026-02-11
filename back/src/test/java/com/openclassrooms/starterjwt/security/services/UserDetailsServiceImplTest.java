package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl - Tests unitaires")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();
    }

    @Test
    @DisplayName("Devrait charger un utilisateur par son email avec succès")
    void testLoadUserByUsername_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(1L);
        assertThat(userDetailsImpl.getFirstName()).isEqualTo("John");
        assertThat(userDetailsImpl.getLastName()).isEqualTo("Doe");

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Devrait lever une exception quand l'utilisateur n'existe pas")
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email: unknown@example.com");

        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    @DisplayName("Devrait charger un utilisateur admin")
    void testLoadUserByUsername_AdminUser() {
        // Given
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminPassword")
                .admin(true)
                .build();

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@example.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("adminPassword");

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(2L);
        assertThat(userDetailsImpl.getFirstName()).isEqualTo("Admin");
        assertThat(userDetailsImpl.getLastName()).isEqualTo("User");

        verify(userRepository, times(1)).findByEmail("admin@example.com");
    }

    @Test
    @DisplayName("Devrait charger plusieurs utilisateurs différents")
    void testLoadUserByUsername_DifferentUsers() {
        // Given
        User user2 = User.builder()
                .id(3L)
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password456")
                .admin(false)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(user2));

        // When
        UserDetails userDetails1 = userDetailsService.loadUserByUsername("test@example.com");
        UserDetails userDetails2 = userDetailsService.loadUserByUsername("user2@example.com");

        // Then
        assertThat(userDetails1.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails2.getUsername()).isEqualTo("user2@example.com");
        assertThat(((UserDetailsImpl) userDetails1).getFirstName()).isEqualTo("John");
        assertThat(((UserDetailsImpl) userDetails2).getFirstName()).isEqualTo("Jane");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).findByEmail("user2@example.com");
    }
}
