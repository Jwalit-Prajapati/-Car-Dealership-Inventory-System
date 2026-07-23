package com.jwalit.inventory_system.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password123");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenMissingFields_thenViolations() {
        RegistrationRequest request = new RegistrationRequest(null, null, null, null);
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(4);
    }

    @Test
    void whenInvalidEmail_thenViolations() {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "invalid-email", "password123");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
    }

    @Test
    void whenPasswordTooShort_thenViolations() {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john@example.com", "short");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
    }
}
