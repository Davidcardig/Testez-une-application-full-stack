package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDetailsImpl - Tests unitaires")
class UserDetailsImplTest {

    @Test
    @DisplayName("Devrait créer un UserDetailsImpl avec le builder")
    void testUserDetailsImplBuilder() {
        // When
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAdmin()).isFalse();
    }

    @Test
    @DisplayName("Devrait retourner une collection vide pour getAuthorities")
    void testGetAuthorities() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When & Then
        assertThat(userDetails.getAuthorities()).isNotNull();
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Devrait retourner true pour isAccountNonExpired")
    void testIsAccountNonExpired() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When & Then
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner true pour isAccountNonLocked")
    void testIsAccountNonLocked() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When & Then
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner true pour isCredentialsNonExpired")
    void testIsCredentialsNonExpired() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When & Then
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner true pour isEnabled")
    void testIsEnabled() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When & Then
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Devrait être égal basé sur l'ID uniquement")
    void testEquals() {
        // Given
        UserDetailsImpl userDetails1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        UserDetailsImpl userDetails2 = UserDetailsImpl.builder()
                .id(1L)
                .username("test2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("differentPassword")
                .admin(true)
                .build();

        UserDetailsImpl userDetails3 = UserDetailsImpl.builder()
                .id(2L)
                .username("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertThat(userDetails1).isEqualTo(userDetails2);
        assertThat(userDetails1).isNotEqualTo(userDetails3);
        assertThat(userDetails1).isEqualTo(userDetails1);
        assertThat(userDetails1).isNotEqualTo(null);
        assertThat(userDetails1).isNotEqualTo("String object");
    }

    @Test
    @DisplayName("Devrait créer un UserDetailsImpl admin")
    void testUserDetailsImplAdmin() {
        // When
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminPassword")
                .admin(true)
                .build();

        // Then
        assertThat(userDetails.getAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait créer un UserDetailsImpl avec tous les arguments")
    void testUserDetailsImplAllArgsConstructor() {
        // When
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@example.com", "John", "Doe", false, "password123");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAdmin()).isFalse();
    }

    @Test
    @DisplayName("Devrait avoir des getters fonctionnels")
    void testGetters() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When & Then
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAdmin()).isFalse();
    }
}
