package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void testValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmailBlank() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidPasswordBlank() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testGettersAndSetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("mypassword");

        assertEquals("user@test.com", loginRequest.getEmail());
        assertEquals("mypassword", loginRequest.getPassword());
    }
}

