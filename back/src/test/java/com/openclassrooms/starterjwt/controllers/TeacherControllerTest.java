package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherController - Tests unitaires")
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
    }

    @Test
    @DisplayName("findById - Devrait retourner un enseignant existant")
    void testFindById_Success() {
        // Arrange
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // Act
        ResponseEntity<?> response = teacherController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(TeacherDto.class);

        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(1)).toDto(any(Teacher.class));
    }

    @Test
    @DisplayName("findById - Devrait retourner 404 si l'enseignant n'existe pas")
    void testFindById_NotFound() {
        // Arrange
        when(teacherService.findById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = teacherController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, never()).toDto(any(Teacher.class));
    }

    @Test
    @DisplayName("findById - Devrait retourner 400 si l'id n'est pas valide")
    void testFindById_InvalidId() {
        // Act
        ResponseEntity<?> response = teacherController.findById("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(teacherService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("findAll - Devrait retourner tous les enseignants")
    void testFindAll_Success() {
        // Arrange
        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
        teacherDto2.setFirstName("Jane");
        teacherDto2.setLastName("Smith");

        List<Teacher> teachers = Arrays.asList(teacher, teacher2);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto, teacherDto2);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        // Act
        ResponseEntity<?> response = teacherController.findAll();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
        assertThat(((List<?>) response.getBody())).hasSize(2);

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(anyList());
    }

    @Test
    @DisplayName("findAll - Devrait retourner une liste vide s'il n'y a pas d'enseignants")
    void testFindAll_EmptyList() {
        // Arrange
        List<Teacher> teachers = Arrays.asList();
        List<TeacherDto> teacherDtos = Arrays.asList();

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(anyList())).thenReturn(teacherDtos);

        // Act
        ResponseEntity<?> response = teacherController.findAll();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
        assertThat(((List<?>) response.getBody())).isEmpty();

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(anyList());
    }
}
