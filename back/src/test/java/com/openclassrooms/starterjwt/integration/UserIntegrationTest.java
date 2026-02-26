package com.openclassrooms.starterjwt.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "david@test.com")
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.admin").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testGetUserWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/user/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testDeleteUserWithInvalidId() throws Exception {
        mockMvc.perform(delete("/api/user/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "david@test.com")
    public void testDeleteUserUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }
}

