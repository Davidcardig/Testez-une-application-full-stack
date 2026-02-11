package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SessionServiceIntegrationTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User user;

    @BeforeEach
    public void setUp() {
        teacher = teacherRepository.findById(1L).orElse(null);
        user = userRepository.findById(2L).orElse(null);
    }

    @Test
    public void testCreateSession() {
        Session session = new Session();
        session.setName("Test Session");
        session.setDescription("Test Description");
        session.setDate(new Date());
        session.setTeacher(teacher);

        Session created = sessionService.create(session);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Session");
    }

    @Test
    public void testGetAllSessions() {
        List<Session> sessions = sessionService.findAll();
        assertThat(sessions).isNotNull();
    }

    @Test
    public void testDeleteSession() {
        Session session = new Session();
        session.setName("To Delete");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(teacher);
        Session created = sessionRepository.save(session);

        sessionService.delete(created.getId());

        Session deleted = sessionService.getById(created.getId());
        assertThat(deleted).isNull();
    }
}


