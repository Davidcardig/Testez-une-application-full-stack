package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SignupRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void testValidSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmailBlank() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidFirstNameBlank() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidLastNameBlank() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("");
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidPasswordBlank() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testGettersAndSetters() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@test.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("mypassword");

        assertEquals("user@test.com", signupRequest.getEmail());
        assertEquals("Jane", signupRequest.getFirstName());
        assertEquals("Smith", signupRequest.getLastName());
        assertEquals("mypassword", signupRequest.getPassword());
    }

    @Test
    public void testInvalidEmailFormat() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid-email");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFirstNameMaxSize() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("A".repeat(21)); // Max is 20
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testLastNameMaxSize() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("D".repeat(21)); // Max is 20
        signupRequest.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
    }
}

