package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService - Tests unitaires")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        teacher1 = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("Devrait retourner tous les professeurs")
    void testFindAll_Success() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<Teacher> result = teacherService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).contains(teacher1, teacher2);
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner une liste vide quand aucun professeur n'existe")
    void testFindAll_EmptyList() {
        // Given
        when(teacherRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Teacher> result = teacherService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner un professeur par son ID")
    void testFindById_Success() {
        // Given
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // When
        Teacher result = teacherService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait retourner null quand le professeur n'existe pas")
    void testFindById_NotFound() {
        // Given
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Teacher result = teacherService.findById(999L);

        // Then
        assertThat(result).isNull();
        verify(teacherRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Devrait retourner le bon professeur parmi plusieurs")
    void testFindById_MultipleTeachers() {
        // Given
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher2));

        // When
        Teacher result = teacherService.findById(2L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        verify(teacherRepository, times(1)).findById(2L);
        verify(teacherRepository, never()).findById(1L);
    }

    @Test
    @DisplayName("Devrait gérer les appels répétés à findAll")
    void testFindAll_MultipleCalls() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<Teacher> result1 = teacherService.findAll();
        List<Teacher> result2 = teacherService.findAll();

        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).hasSize(2);
        verify(teacherRepository, times(2)).findAll();
    }
}
