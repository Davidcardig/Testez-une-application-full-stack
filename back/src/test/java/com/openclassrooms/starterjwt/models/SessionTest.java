package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Session - Tests unitaires")
class SessionTest {

    @Test
    @DisplayName("Devrait créer une session avec le builder")
    void testSessionBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Date sessionDate = new Date();
        Teacher teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        List<User> users = new ArrayList<>();

        // When
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(sessionDate)
                .description("Test session")
                .teacher(teacher)
                .users(users)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Yoga Session");
        assertThat(session.getDate()).isEqualTo(sessionDate);
        assertThat(session.getDescription()).isEqualTo("Test session");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).isEqualTo(users);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait créer une session avec le constructeur par défaut")
    void testSessionDefaultConstructor() {
        // When
        Session session = new Session();

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getId()).isNull();
        assertThat(session.getName()).isNull();
        assertThat(session.getDate()).isNull();
        assertThat(session.getDescription()).isNull();
        assertThat(session.getTeacher()).isNull();
        assertThat(session.getUsers()).isNull();
    }

    @Test
    @DisplayName("Devrait créer une session avec tous les arguments")
    void testSessionAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Date sessionDate = new Date();
        Teacher teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        List<User> users = new ArrayList<>();

        // When
        Session session = new Session(1L, "Yoga Session", sessionDate, "Test session", teacher, users, now, now);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Yoga Session");
        assertThat(session.getDate()).isEqualTo(sessionDate);
        assertThat(session.getDescription()).isEqualTo("Test session");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).isEqualTo(users);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait permettre de modifier les attributs avec les setters")
    void testSessionSetters() {
        // Given
        Session session = new Session();
        LocalDateTime now = LocalDateTime.now();
        Date sessionDate = new Date();
        Teacher teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        List<User> users = new ArrayList<>();

        // When
        session.setId(1L);
        session.setName("Meditation Session");
        session.setDate(sessionDate);
        session.setDescription("Relaxation session");
        session.setTeacher(teacher);
        session.setUsers(users);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        // Then
        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Meditation Session");
        assertThat(session.getDate()).isEqualTo(sessionDate);
        assertThat(session.getDescription()).isEqualTo("Relaxation session");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).isEqualTo(users);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait supporter le chaînage avec Accessors")
    void testSessionChaining() {
        // Given
        Date sessionDate = new Date();
        Teacher teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();

        // When
        Session session = new Session()
                .setId(1L)
                .setName("Yoga Session")
                .setDate(sessionDate)
                .setDescription("Test session")
                .setTeacher(teacher);

        // Then
        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Yoga Session");
        assertThat(session.getDate()).isEqualTo(sessionDate);
        assertThat(session.getDescription()).isEqualTo("Test session");
        assertThat(session.getTeacher()).isEqualTo(teacher);
    }

    @Test
    @DisplayName("Devrait être égal basé sur l'ID uniquement")
    void testSessionEquals() {
        // Given
        Session session1 = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .description("Description 1")
                .build();

        Session session2 = Session.builder()
                .id(1L)
                .name("Meditation Session")
                .description("Description 2")
                .build();

        Session session3 = Session.builder()
                .id(2L)
                .name("Yoga Session")
                .description("Description 1")
                .build();

        // Then
        assertThat(session1).isEqualTo(session2);
        assertThat(session1).isNotEqualTo(session3);
        assertThat(session1.hashCode()).isEqualTo(session2.hashCode());
    }

    @Test
    @DisplayName("Devrait générer un toString valide")
    void testSessionToString() {
        // Given
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .description("Test session")
                .build();

        // When
        String toString = session.toString();

        // Then
        assertThat(toString).contains("Session");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("name=Yoga Session");
        assertThat(toString).contains("description=Test session");
    }

    @Test
    @DisplayName("Devrait permettre d'ajouter des utilisateurs à une session")
    void testSessionAddUsers() {
        // Given
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .users(new ArrayList<>())
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        // When
        session.getUsers().add(user);

        // Then
        assertThat(session.getUsers()).hasSize(1);
        assertThat(session.getUsers()).contains(user);
    }

    @Test
    @DisplayName("Devrait permettre de retirer des utilisateurs d'une session")
    void testSessionRemoveUsers() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        List<User> users = new ArrayList<>();
        users.add(user);

        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .users(users)
                .build();

        // When
        session.getUsers().remove(user);

        // Then
        assertThat(session.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("Devrait créer une session avec builder partiel")
    void testSessionBuilderPartial() {
        // When
        Session session = Session.builder()
                .name("Yoga Session")
                .description("Test session")
                .build();

        // Then
        assertThat(session.getId()).isNull();
        assertThat(session.getName()).isEqualTo("Yoga Session");
        assertThat(session.getDescription()).isEqualTo("Test session");
        assertThat(session.getDate()).isNull();
        assertThat(session.getTeacher()).isNull();
        assertThat(session.getUsers()).isNull();
    }
}
