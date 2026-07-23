package com.jwalit.inventory_system.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResponseTest {

    @Test
    @DisplayName("Should create a valid success response with only data")
    void shouldCreateSuccessResponseWithData() {
        // Arrange
        final Integer mockPayload = 42;

        // Act
        ApiResponse<Integer> actual = ApiResponse.success(mockPayload);

        // Assert
        assertAll("Verify default success properties",
            () -> assertTrue(actual.isSuccess()),
            () -> assertEquals(200, actual.getStatus()),
            () -> assertEquals("Success", actual.getMessage()),
            () -> assertEquals(mockPayload, actual.getData())
        );
    }

    @Test
    @DisplayName("Should create a valid success response with a custom message and data")
    void shouldCreateSuccessResponseWithMessageAndData() {
        // Arrange
        final String expectedPayload = "Payload";
        final String expectedMsg = "Operation completed";

        // Act
        ApiResponse<String> actual = ApiResponse.success(expectedMsg, expectedPayload);

        // Assert
        assertAll("Verify custom message success properties",
            () -> assertTrue(actual.isSuccess()),
            () -> assertEquals(200, actual.getStatus()),
            () -> assertEquals(expectedMsg, actual.getMessage()),
            () -> assertEquals(expectedPayload, actual.getData())
        );
    }

    @Test
    @DisplayName("Should create a valid error response without data")
    void shouldCreateErrorResponse() {
        // Arrange
        final int expectedCode = 500;
        final String expectedErrorMsg = "Internal Server Error";

        // Act
        ApiResponse<Void> actual = ApiResponse.error(expectedCode, expectedErrorMsg);

        // Assert
        assertAll("Verify error properties",
            () -> assertFalse(actual.isSuccess()),
            () -> assertEquals(expectedCode, actual.getStatus()),
            () -> assertEquals(expectedErrorMsg, actual.getMessage()),
            () -> assertNull(actual.getData())
        );
    }

    @Test
    @DisplayName("Should ensure timestamp is populated automatically upon instantiation")
    void shouldPopulateTimestampAutomatically() {
        // Arrange
        Instant start = Instant.now();

        // Act
        ApiResponse<Object> actual = ApiResponse.success(new Object());

        // Assert
        Instant end = Instant.now();
        Instant recorded = actual.getTimestamp();

        assertNotNull(recorded);
        assertFalse(recorded.isBefore(start));
        assertFalse(recorded.isAfter(end));
    }
}
