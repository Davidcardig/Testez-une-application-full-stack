package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageResponseTest {

    @Test
    public void testConstructorAndGetter() {
        String message = "User registered successfully!";
        MessageResponse messageResponse = new MessageResponse(message);

        assertEquals(message, messageResponse.getMessage());
    }

    @Test
    public void testSetter() {
        MessageResponse messageResponse = new MessageResponse("Initial message");

        String newMessage = "Updated message";
        messageResponse.setMessage(newMessage);

        assertEquals(newMessage, messageResponse.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        MessageResponse messageResponse = new MessageResponse("");
        assertEquals("", messageResponse.getMessage());
    }

    @Test
    public void testNullMessage() {
        MessageResponse messageResponse = new MessageResponse(null);
        assertNull(messageResponse.getMessage());
    }

    @Test
    public void testLongMessage() {
        String longMessage = "This is a very long message that contains a lot of text to test if the MessageResponse can handle long strings properly without any issues.";
        MessageResponse messageResponse = new MessageResponse(longMessage);
        assertEquals(longMessage, messageResponse.getMessage());
    }

    @Test
    public void testSuccessMessage() {
        MessageResponse messageResponse = new MessageResponse("Operation completed successfully");
        assertTrue(messageResponse.getMessage().contains("successfully"));
    }

    @Test
    public void testErrorMessage() {
        MessageResponse messageResponse = new MessageResponse("Error: Invalid input");
        assertTrue(messageResponse.getMessage().contains("Error"));
    }
}

