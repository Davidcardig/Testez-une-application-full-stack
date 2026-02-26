package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionMapper sessionMapper;

    private Teacher teacher;

    @BeforeEach
    public void setUp() {

        teacher = teacherRepository.findById(1L).orElse(null);
    }

    @Test
    @WithMockUser
    public void testGetAllSessions() throws Exception {
        // Given
        Session session1 = Session.builder()
                .name("Morning Yoga")
                .description("Morning yoga session")
                .date(new Date())
                .teacher(teacher)
                .build();
        session1 = sessionRepository.save(session1);

        Session session2 = Session.builder()
                .name("Evening Yoga")
                .description("Evening yoga session")
                .date(new Date())
                .teacher(teacher)
                .build();
        session2 = sessionRepository.save(session2);

        // When
        List<Session> sessions = sessionRepository.findAll();

        // Then
        assertThat(session1.getId()).isNotNull();
        assertThat(session2.getId()).isNotNull();

        assertThat(sessions)
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting(Session::getId, Session::getName, Session::getDescription, Session::getTeacher, Session::getDate)
                .contains(
                        tuple(session1.getId(), "Morning Yoga", "Morning yoga session", teacher, session1.getDate()),
                        tuple(session2.getId(), "Evening Yoga", "Evening yoga session", teacher, session2.getDate())
                );
    }

    @Test
    @WithMockUser
    public void testGetSessionById() throws Exception {
        // Given - Create a test session
        Session session = Session.builder()
                .name("Test Session")
        .description("Test Description for session")
        .date(new Date())
        .teacher(teacher)
        .build();
        session = sessionRepository.save(session);

        // When & Then
        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Test Session"))
                .andExpect(jsonPath("$.description").value("Test Description for session"))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()))
                .andExpect(jsonPath("$.users").isArray());
    }

    @Test
    @WithMockUser
    public void testGetSessionByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testGetSessionWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/session/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testCreateSession() throws Exception {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New Yoga Session");
        sessionDto.setDescription("A relaxing session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(teacher.getId());

        // When
        var when = mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)));

        // Then
        when.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Yoga Session"))
                .andExpect(jsonPath("$.description").value("A relaxing session"))
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser
    public void testDeleteSession() throws Exception {
        // First create a session
        Session session = Session.builder()
                .name("Session to delete")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);
        Long sessionId = session.getId();

        mockMvc.perform(delete("/api/session/" + sessionId))
                .andExpect(status().isOk());

        // Verify the session is actually deleted
        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }

    @Test
    @WithMockUser
    public void testDeleteSessionNotFound() throws Exception {
        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testUpdateSession() throws Exception {
        // Given - Create a session
        Session session = Session.builder()
                .name("Original Session")
                .description("Original Description")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);

        // Prepare update DTO
        SessionDto updateDto = new SessionDto();
        updateDto.setName("Updated Session");
        updateDto.setDescription("Updated Description");
        updateDto.setDate(new Date());
        updateDto.setTeacher_id(teacher.getId());

        // When & Then
        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()));
    }

    @Test
    @WithMockUser
    public void testParticipate() throws Exception {
        // Given - Create a session
        Session session = Session.builder()
                .name("Participation Session")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);

        // Create a user
        User user = userRepository.findById(2L).orElse(null);
        assertThat(user).isNotNull();

        // When & Then - User participates
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        // Verify user is in the session
        Session updatedSession = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).hasSize(1);
        assertThat(updatedSession.getUsers().get(0).getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUser
    public void testParticipateSessionNotFound() throws Exception {
        mockMvc.perform(post("/api/session/999/participate/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testParticipateUserNotFound() throws Exception {
        // Given - Create a session
        Session session = Session.builder()
                .name("Test Session")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testParticipateAlreadyParticipating() throws Exception {
        // Given - Create a session with a participant
        Session session = Session.builder()
                .name("Test Session")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);

        User user = userRepository.findById(2L).orElse(null);

        // User participates once
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        // When & Then - User tries to participate again
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipate() throws Exception {
        // Given - Create a session with a participant
        Session session = Session.builder()
                .name("Test Session")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);

        User user = userRepository.findById(2L).orElse(null);

        // User participates
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        // When & Then - User no longer participates
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        // Verify user is removed from the session
        Session updatedSession = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).isEmpty();
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipateSessionNotFound() throws Exception {
        mockMvc.perform(delete("/api/session/999/participate/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipateNotParticipating() throws Exception {
        // Préparation - Créer une session sans participants
        Session session = Session.builder()
                .name("Test Session")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .build();
        session = sessionRepository.save(session);

        // Exécution et vérification - L'utilisateur essaie d'arrêter de participer sans être participant
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/2"))
                .andExpect(status().isBadRequest());
    }
}


