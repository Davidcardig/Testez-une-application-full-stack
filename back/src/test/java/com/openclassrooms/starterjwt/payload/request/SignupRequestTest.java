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

    @Test
    public void testEqualsAndHashCode() {
        SignupRequest req1 = new SignupRequest();
        req1.setEmail("test@example.com");
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setEmail("test@example.com");
        req2.setFirstName("John");
        req2.setLastName("Doe");
        req2.setPassword("password123");

        SignupRequest req3 = new SignupRequest();
        req3.setEmail("other@example.com");
        req3.setFirstName("Jane");
        req3.setLastName("Smith");
        req3.setPassword("different");

        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1.hashCode(), req3.hashCode());
    }

    @Test
    public void testEqualsWithNull() {
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("password123");

        assertNotEquals(req, null);
        assertEquals(req, req);
    }

    @Test
    public void testEqualsWithDifferentType() {
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("password123");

        assertNotEquals(req, new Object());
    }

    @Test
    public void testEqualsWithDifferentEmail() {
        SignupRequest req1 = new SignupRequest();
        req1.setEmail("test1@example.com");
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setEmail("test2@example.com");
        req2.setFirstName("John");
        req2.setLastName("Doe");
        req2.setPassword("password123");

        assertNotEquals(req1, req2);
    }

    @Test
    public void testEqualsWithDifferentFirstName() {
        SignupRequest req1 = new SignupRequest();
        req1.setEmail("test@example.com");
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setEmail("test@example.com");
        req2.setFirstName("Jane");
        req2.setLastName("Doe");
        req2.setPassword("password123");

        assertNotEquals(req1, req2);
    }

    @Test
    public void testEqualsWithDifferentLastName() {
        SignupRequest req1 = new SignupRequest();
        req1.setEmail("test@example.com");
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setEmail("test@example.com");
        req2.setFirstName("John");
        req2.setLastName("Smith");
        req2.setPassword("password123");

        assertNotEquals(req1, req2);
    }

    @Test
    public void testEqualsWithDifferentPassword() {
        SignupRequest req1 = new SignupRequest();
        req1.setEmail("test@example.com");
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setEmail("test@example.com");
        req2.setFirstName("John");
        req2.setLastName("Doe");
        req2.setPassword("differentPassword");

        assertNotEquals(req1, req2);
    }

    @Test
    public void testToString() {
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("password123");

        String toString = req.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
    }

    @Test
    public void testHashCodeWithNullFields() {
        SignupRequest req1 = new SignupRequest();
        SignupRequest req2 = new SignupRequest();

        assertEquals(req1.hashCode(), req2.hashCode());
        assertEquals(req1, req2);
    }

    @Test
    public void testEqualsWithNullEmailInBoth() {
        SignupRequest req1 = new SignupRequest();
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setFirstName("John");
        req2.setLastName("Doe");
        req2.setPassword("password123");

        assertEquals(req1, req2);
    }

    @Test
    public void testEqualsOneNullEmailOneNot() {
        SignupRequest req1 = new SignupRequest();
        req1.setFirstName("John");
        req1.setLastName("Doe");
        req1.setPassword("password123");

        SignupRequest req2 = new SignupRequest();
        req2.setEmail("test@example.com");
        req2.setFirstName("John");
        req2.setLastName("Doe");
        req2.setPassword("password123");

        assertNotEquals(req1, req2);
    }
}

