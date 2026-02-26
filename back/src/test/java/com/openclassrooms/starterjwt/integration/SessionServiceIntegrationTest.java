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
        // Given
        Session session = Session.builder()
                .name("Test Session")
                .description("Test Description")
                .date(new Date())
                .teacher(teacher)
                .build();

        // When
        Session created = sessionService.create(session);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Session");
        assertThat(created.getDescription()).isEqualTo("Test Description");
        assertThat(created.getDate()).isNotNull();
        assertThat(created.getTeacher()).isNotNull();
        assertThat(created.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testGetAllSessions() {
        // Given - Create test sessions
        sessionRepository.save(Session.builder()
                .name("Morning Session")
                .description("Morning yoga")
                .date(new Date())
                .teacher(teacher)
                .build());

        sessionRepository.save(Session.builder()
                .name("Evening Session")
                .description("Evening yoga")
                .date(new Date())
                .teacher(teacher)
                .build());

        // When
        List<Session> sessions = sessionService.findAll();

        // Then
        assertThat(sessions).isNotNull();
        assertThat(sessions).isNotEmpty();
        assertThat(sessions.size()).isGreaterThanOrEqualTo(2);

        // Verify session properties
        for (Session session : sessions) {
            assertThat(session.getId()).isNotNull();
            assertThat(session.getName()).isNotNull();
            assertThat(session.getDescription()).isNotNull();
            assertThat(session.getTeacher()).isNotNull();
            assertThat(session.getDate()).isNotNull();
        }
    }

    @Test
    public void testDeleteSession() {
        // Given - Create a session
        Session created = sessionRepository.save(Session.builder()
                .name("To Delete")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build());
        Long sessionId = created.getId();

        // Verify session exists before deletion
        assertThat(sessionService.getById(sessionId)).isNotNull();

        // When - Delete the session
        sessionService.delete(sessionId);

        // Then - Verify session is deleted
        Session deleted = sessionService.getById(sessionId);
        assertThat(deleted).isNull();
        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }

    @Test
    public void testGetSessionById() {
        // Given - Create a session
        Session created = sessionRepository.save(Session.builder()
                .name("Get By Id Test")
                .description("Testing getById")
                .date(new Date())
                .teacher(teacher)
                .build());

        // When
        Session found = sessionService.getById(created.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Get By Id Test");
        assertThat(found.getDescription()).isEqualTo("Testing getById");
        assertThat(found.getTeacher()).isNotNull();
        assertThat(found.getTeacher().getId()).isEqualTo(teacher.getId());
    }

    @Test
    public void testGetSessionByIdNotFound() {
        // When
        Session found = sessionService.getById(999L);

        // Then
        assertThat(found).isNull();
    }

    @Test
    public void testUpdateSession() {
        // Given - Create a session
        Session created = sessionRepository.save(Session.builder()
                .name("Original Name")
                .description("Original Description")
                .date(new Date())
                .teacher(teacher)
                .build());

        // When - Update the session
        created.setName("Updated Name");
        created.setDescription("Updated Description");
        Session updated = sessionService.update(created.getId(), created);

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testParticipate() {
        // Given - Create a session
        Session created = sessionRepository.save(Session.builder()
                .name("Participation Test")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build());

        // When - User participates
        sessionService.participate(created.getId(), user.getId());

        // Then - Verify user is in the session
        Session updated = sessionService.getById(created.getId());
        assertThat(updated.getUsers()).isNotNull();
        assertThat(updated.getUsers()).hasSize(1);
        assertThat(updated.getUsers().get(0).getId()).isEqualTo(user.getId());
    }

    @Test
    public void testNoLongerParticipate() {
        // Given - Créer une session avec un participant
        Session created = sessionRepository.save(Session.builder()
                .name("No Longer Participate Test")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build());
        sessionService.participate(created.getId(), user.getId());

        // Vérifier que l'utilisateur participe
        Session withUser = sessionService.getById(created.getId());
        assertThat(withUser.getUsers()).hasSize(1);

        // When - L'utilisateur ne participe plus
        sessionService.noLongerParticipate(created.getId(), user.getId());

        // Then - Vérifier que l'utilisateur est retiré de la session
        Session withoutUser = sessionService.getById(created.getId());
        assertThat(withoutUser.getUsers()).isEmpty();
    }
}

