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
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
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
        //given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New Yoga Session");
        sessionDto.setDescription("A relaxing session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);

        //when
       var when = mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(sessionDto))) ;
       //then
       when.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("New Yoga Session"));
    }

    @Test
    @WithMockUser
    public void testDeleteSession() throws Exception {
        // First create a session
        Session session = new Session();
        session.setName("Session to delete");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDeleteSessionNotFound() throws Exception {
        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }
}


