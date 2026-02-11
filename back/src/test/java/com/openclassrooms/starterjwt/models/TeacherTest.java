package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Teacher - Tests unitaires")
class TeacherTest {

    @Test
    @DisplayName("Devrait créer un professeur avec le builder")
    void testTeacherBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait créer un professeur avec le constructeur par défaut")
    void testTeacherDefaultConstructor() {
        // When
        Teacher teacher = new Teacher();

        // Then
        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isNull();
        assertThat(teacher.getFirstName()).isNull();
        assertThat(teacher.getLastName()).isNull();
    }

    @Test
    @DisplayName("Devrait créer un professeur avec tous les arguments")
    void testTeacherAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        Teacher teacher = new Teacher(1L, "Doe", "John", now, now);

        // Then
        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait permettre de modifier les attributs avec les setters")
    void testTeacherSetters() {
        // Given
        Teacher teacher = new Teacher();
        LocalDateTime now = LocalDateTime.now();

        // When
        teacher.setId(1L);
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);

        // Then
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("Jane");
        assertThat(teacher.getLastName()).isEqualTo("Smith");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait supporter le chaînage avec Accessors")
    void testTeacherChaining() {
        // When
        Teacher teacher = new Teacher()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Doe");

        // Then
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Devrait être égal basé sur l'ID uniquement")
    void testTeacherEquals() {
        // Given
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        Teacher teacher2 = Teacher.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        Teacher teacher3 = Teacher.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .build();

        // Then
        assertThat(teacher1).isEqualTo(teacher2);
        assertThat(teacher1).isNotEqualTo(teacher3);
        assertThat(teacher1.hashCode()).isEqualTo(teacher2.hashCode());
    }

    @Test
    @DisplayName("Devrait générer un toString valide")
    void testTeacherToString() {
        // Given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        // When
        String toString = teacher.toString();

        // Then
        assertThat(toString).contains("Teacher");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("firstName=John");
        assertThat(toString).contains("lastName=Doe");
    }

    @Test
    @DisplayName("Devrait permettre de créer un professeur avec builder partiel")
    void testTeacherBuilderPartial() {
        // When
        Teacher teacher = Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        // Then
        assertThat(teacher.getId()).isNull();
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isNull();
        assertThat(teacher.getUpdatedAt()).isNull();
    }
}
