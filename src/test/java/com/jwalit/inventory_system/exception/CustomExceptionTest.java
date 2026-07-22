package com.jwalit.inventory_system.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionTest {

    @Test
    void shouldCreateResourceNotFoundException() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        assertNotNull(exception);
    }


    @Test
    void shouldExtendRuntimeException() {
        ResourceNotFoundException notFoundException = new ResourceNotFoundException("test");

        assertTrue(notFoundException instanceof RuntimeException);
    }

    @Test
    void shouldPreserveExceptionMessage() {
        String notFoundMessage = "Not found message";

        ResourceNotFoundException notFoundException = new ResourceNotFoundException(notFoundMessage);

        assertEquals(notFoundMessage, notFoundException.getMessage());
    }
}
