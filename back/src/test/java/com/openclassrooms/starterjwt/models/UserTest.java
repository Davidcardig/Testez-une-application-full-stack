package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User - Tests unitaires")
class UserTest {

    @Test
    @DisplayName("Devrait créer un utilisateur avec le builder")
    void testUserBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait créer un utilisateur avec le constructeur par défaut")
    void testUserDefaultConstructor() {
        // When
        User user = new User();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPassword()).isNull();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur avec le constructeur requis")
    void testUserRequiredArgsConstructor() {
        // When
        User user = new User("test@example.com", "Doe", "John", "password123", true);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur avec tous les arguments")
    void testUserAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        User user = new User(1L, "test@example.com", "Doe", "John", "password123", false, now, now);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait permettre de modifier les attributs avec les setters")
    void testUserSetters() {
        // Given
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        // When
        user.setId(1L);
        user.setEmail("updated@example.com");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPassword("newPassword");
        user.setAdmin(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("updated@example.com");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getPassword()).isEqualTo("newPassword");
        assertThat(user.isAdmin()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait supporter le chaînage avec Accessors")
    void testUserChaining() {
        // When
        User user = new User()
                .setId(1L)
                .setEmail("test@example.com")
                .setFirstName("John")
                .setLastName("Doe")
                .setPassword("password123")
                .setAdmin(false);

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Devrait être égal basé sur l'ID uniquement")
    void testUserEquals() {
        // Given
        User user1 = User.builder()
                .id(1L)
                .email("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("test2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("differentPassword")
                .admin(true)
                .build();

        User user3 = User.builder()
                .id(2L)
                .email("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("Devrait générer un toString valide")
    void testUserToString() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When
        String toString = user.toString();

        // Then
        assertThat(toString).contains("User");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("email=test@example.com");
        assertThat(toString).contains("firstName=John");
        assertThat(toString).contains("lastName=Doe");
    }

    @Test
    @DisplayName("Devrait créer un utilisateur administrateur")
    void testUserAdmin() {
        // When
        User user = User.builder()
                .id(1L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminPassword")
                .admin(true)
                .build();

        // Then
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur non-administrateur")
    void testUserNonAdmin() {
        // When
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("Regular")
                .lastName("User")
                .password("userPassword")
                .admin(false)
                .build();

        // Then
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Devrait permettre de créer un utilisateur avec builder partiel")
    void testUserBuilderPartial() {
        // When
        User user = User.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Devrait comparer correctement les hashCodes")
    void testUserHashCode() {
        // Given
        User user1 = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();
        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();
        User user3 = User.builder()
                .id(2L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
    }

    @Test
    @DisplayName("Devrait gérer l'égalité avec null et même instance")
    void testUserEqualsEdgeCases() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertThat(user.equals(user)).isTrue();
        assertThat(user.equals(null)).isFalse();
        assertThat(user.equals(new Object())).isFalse();
    }

    @Test
    @DisplayName("Devrait permettre de modifier l'email")
    void testUserEmailModification() {
        // Given
        User user = User.builder()
                .email("old@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When
        user.setEmail("new@example.com");

        // Then
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Devrait permettre de modifier le statut admin")
    void testUserAdminStatusModification() {
        // Given
        User user = User.builder()
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When
        user.setAdmin(true);

        // Then
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait permettre de modifier le mot de passe")
    void testUserPasswordModification() {
        // Given
        User user = User.builder()
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("oldPassword")
                .admin(false)
                .build();

        // When
        user.setPassword("newPassword");

        // Then
        assertThat(user.getPassword()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("Devrait gérer correctement les timestamps")
    void testUserTimestamps() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        User user = new User();

        // When
        user.setCreatedAt(before);
        user.setUpdatedAt(before);

        // Then
        assertThat(user.getCreatedAt()).isEqualTo(before);
        assertThat(user.getUpdatedAt()).isEqualTo(before);
    }
}
