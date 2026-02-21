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

    @Test
    @DisplayName("Devrait comparer correctement les hashCodes")
    void testSessionHashCode() {
        // Given
        Session session1 = Session.builder().id(1L).build();
        Session session2 = Session.builder().id(1L).build();
        Session session3 = Session.builder().id(2L).build();

        // Then
        assertThat(session1.hashCode()).isEqualTo(session2.hashCode());
        assertThat(session1.hashCode()).isNotEqualTo(session3.hashCode());
    }

    @Test
    @DisplayName("Devrait gérer l'égalité avec null et même instance")
    void testSessionEqualsEdgeCases() {
        // Given
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .description("Test session")
                .build();

        // Then
        assertThat(session.equals(session)).isTrue();
        assertThat(session.equals(null)).isFalse();
        assertThat(session.equals(new Object())).isFalse();
    }

    @Test
    @DisplayName("Devrait permettre de modifier le nom")
    void testSessionNameModification() {
        // Given
        Session session = Session.builder()
                .name("Yoga Session")
                .description("Test")
                .build();

        // When
        session.setName("Meditation Session");

        // Then
        assertThat(session.getName()).isEqualTo("Meditation Session");
    }

    @Test
    @DisplayName("Devrait permettre de modifier la description")
    void testSessionDescriptionModification() {
        // Given
        Session session = Session.builder()
                .name("Yoga Session")
                .description("Old description")
                .build();

        // When
        session.setDescription("New description");

        // Then
        assertThat(session.getDescription()).isEqualTo("New description");
    }

    @Test
    @DisplayName("Devrait permettre de modifier la date")
    void testSessionDateModification() {
        // Given
        Date oldDate = new Date();
        Date newDate = new Date(System.currentTimeMillis() + 86400000);
        Session session = Session.builder()
                .name("Yoga Session")
                .date(oldDate)
                .build();

        // When
        session.setDate(newDate);

        // Then
        assertThat(session.getDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Devrait permettre de modifier le professeur")
    void testSessionTeacherModification() {
        // Given
        Teacher oldTeacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        Teacher newTeacher = Teacher.builder().id(2L).firstName("Jane").lastName("Smith").build();
        Session session = Session.builder()
                .name("Yoga Session")
                .teacher(oldTeacher)
                .build();

        // When
        session.setTeacher(newTeacher);

        // Then
        assertThat(session.getTeacher()).isEqualTo(newTeacher);
        assertThat(session.getTeacher().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Devrait gérer une session sans utilisateurs")
    void testSessionWithoutUsers() {
        // Given
        Session session = Session.builder()
                .name("Yoga Session")
                .description("Test")
                .build();

        // Then
        assertThat(session.getUsers()).isNull();
    }

    @Test
    @DisplayName("Devrait gérer une session avec une liste vide d'utilisateurs")
    void testSessionWithEmptyUsersList() {
        // Given
        Session session = Session.builder()
                .name("Yoga Session")
                .users(new ArrayList<>())
                .build();

        // Then
        assertThat(session.getUsers()).isNotNull();
        assertThat(session.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("Devrait gérer correctement les timestamps")
    void testSessionTimestamps() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        Session session = new Session();

        // When
        session.setCreatedAt(before);
        session.setUpdatedAt(before);

        // Then
        assertThat(session.getCreatedAt()).isEqualTo(before);
        assertThat(session.getUpdatedAt()).isEqualTo(before);
    }

    @Test
    @DisplayName("Devrait permettre d'ajouter plusieurs utilisateurs")
    void testSessionAddMultipleUsers() {
        // Given
        Session session = Session.builder()
                .name("Yoga Session")
                .users(new ArrayList<>())
                .build();

        User user1 = User.builder().id(1L).email("user1@test.com").firstName("John").lastName("Doe").password("pass1").admin(false).build();
        User user2 = User.builder().id(2L).email("user2@test.com").firstName("Jane").lastName("Smith").password("pass2").admin(false).build();

        // When
        session.getUsers().add(user1);
        session.getUsers().add(user2);

        // Then
        assertThat(session.getUsers()).hasSize(2);
        assertThat(session.getUsers()).containsExactly(user1, user2);
    }
}
