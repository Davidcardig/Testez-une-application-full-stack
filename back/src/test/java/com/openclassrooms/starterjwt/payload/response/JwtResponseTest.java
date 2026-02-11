package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtResponseTest {

    @Test
    public void testConstructorAndGetters() {
        String token = "test-jwt-token";
        Long id = 1L;
        String username = "user@test.com";
        String firstName = "John";
        String lastName = "Doe";
        Boolean admin = true;

        JwtResponse jwtResponse = new JwtResponse(token, id, username, firstName, lastName, admin);

        assertEquals(token, jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getType());
        assertEquals(id, jwtResponse.getId());
        assertEquals(username, jwtResponse.getUsername());
        assertEquals(firstName, jwtResponse.getFirstName());
        assertEquals(lastName, jwtResponse.getLastName());
        assertEquals(admin, jwtResponse.getAdmin());
    }

    @Test
    public void testSetters() {
        JwtResponse jwtResponse = new JwtResponse("token", 1L, "user@test.com", "John", "Doe", false);

        jwtResponse.setToken("new-token");
        jwtResponse.setType("Custom");
        jwtResponse.setId(2L);
        jwtResponse.setUsername("newuser@test.com");
        jwtResponse.setFirstName("Jane");
        jwtResponse.setLastName("Smith");
        jwtResponse.setAdmin(true);

        assertEquals("new-token", jwtResponse.getToken());
        assertEquals("Custom", jwtResponse.getType());
        assertEquals(2L, jwtResponse.getId());
        assertEquals("newuser@test.com", jwtResponse.getUsername());
        assertEquals("Jane", jwtResponse.getFirstName());
        assertEquals("Smith", jwtResponse.getLastName());
        assertTrue(jwtResponse.getAdmin());
    }

    @Test
    public void testDefaultType() {
        JwtResponse jwtResponse = new JwtResponse("token", 1L, "user@test.com", "John", "Doe", false);
        assertEquals("Bearer", jwtResponse.getType());
    }

    @Test
    public void testAdminFalse() {
        JwtResponse jwtResponse = new JwtResponse("token", 1L, "user@test.com", "John", "Doe", false);
        assertFalse(jwtResponse.getAdmin());
    }

    @Test
    public void testAdminTrue() {
        JwtResponse jwtResponse = new JwtResponse("token", 1L, "admin@test.com", "Admin", "User", true);
        assertTrue(jwtResponse.getAdmin());
    }
}

