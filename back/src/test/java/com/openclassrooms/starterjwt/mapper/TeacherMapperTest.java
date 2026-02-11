package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherMapper - Tests unitaires")
class TeacherMapperTest {

    private TeacherMapper teacherMapper;

    @BeforeEach
    void setUp() {
        teacherMapper = Mappers.getMapper(TeacherMapper.class);
    }

    @Test
    @DisplayName("Devrait convertir Teacher en TeacherDto")
    void testToDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        // Then
        assertThat(teacherDto).isNotNull();
        assertThat(teacherDto.getId()).isEqualTo(1L);
        assertThat(teacherDto.getFirstName()).isEqualTo("John");
        assertThat(teacherDto.getLastName()).isEqualTo("Doe");
        assertThat(teacherDto.getCreatedAt()).isEqualTo(now);
        assertThat(teacherDto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait convertir TeacherDto en Teacher")
    void testToEntity() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        TeacherDto teacherDto = new TeacherDto(1L, "Doe", "John", now, now);

        // When
        Teacher teacher = teacherMapper.toEntity(teacherDto);

        // Then
        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait convertir une liste de Teachers en liste de TeacherDtos")
    void testToDtoList() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        // When
        List<TeacherDto> teacherDtos = teacherMapper.toDto(teachers);

        // Then
        assertThat(teacherDtos).isNotNull();
        assertThat(teacherDtos).hasSize(2);
        assertThat(teacherDtos.get(0).getId()).isEqualTo(1L);
        assertThat(teacherDtos.get(0).getFirstName()).isEqualTo("John");
        assertThat(teacherDtos.get(1).getId()).isEqualTo(2L);
        assertThat(teacherDtos.get(1).getFirstName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Devrait convertir une liste de TeacherDtos en liste de Teachers")
    void testToEntityList() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        TeacherDto teacherDto1 = new TeacherDto(1L, "Doe", "John", now, now);
        TeacherDto teacherDto2 = new TeacherDto(2L, "Smith", "Jane", now, now);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

        // When
        List<Teacher> teachers = teacherMapper.toEntity(teacherDtos);

        // Then
        assertThat(teachers).isNotNull();
        assertThat(teachers).hasSize(2);
        assertThat(teachers.get(0).getId()).isEqualTo(1L);
        assertThat(teachers.get(0).getFirstName()).isEqualTo("John");
        assertThat(teachers.get(1).getId()).isEqualTo(2L);
        assertThat(teachers.get(1).getFirstName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Devrait retourner null si Teacher est null")
    void testToDto_NullTeacher() {
        // When
        TeacherDto teacherDto = teacherMapper.toDto((Teacher) null);

        // Then
        assertThat(teacherDto).isNull();
    }

    @Test
    @DisplayName("Devrait retourner null si TeacherDto est null")
    void testToEntity_NullDto() {
        // When
        Teacher teacher = teacherMapper.toEntity((TeacherDto) null);

        // Then
        assertThat(teacher).isNull();
    }
}
