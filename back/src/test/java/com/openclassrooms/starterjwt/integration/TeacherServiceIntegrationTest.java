package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeacherServiceIntegrationTest {

    @Autowired
    private TeacherService teacherService;

    @Test
    public void testFindAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();
        assertThat(teachers).isNotNull();
        assertThat(teachers).isNotEmpty();
    }

    @Test
    public void testFindTeacherById() {
        Teacher teacher = teacherService.findById(1L);
        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isNotNull();
        assertThat(teacher.getLastName()).isNotNull();
    }

    @Test
    public void testFindTeacherByIdNotFound() {
        Teacher teacher = teacherService.findById(999L);
        assertThat(teacher).isNull();
    }
}

