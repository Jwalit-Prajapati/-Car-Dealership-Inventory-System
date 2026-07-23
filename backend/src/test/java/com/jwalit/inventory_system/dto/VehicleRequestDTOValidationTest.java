package com.jwalit.inventory_system.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleRequestDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "Camry", "Sedan", new BigDecimal("25000"), 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenMakeIsNull_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO(null, "Camry", "Sedan", new BigDecimal("25000"), 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenMakeIsBlank_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("", "Camry", "Sedan", new BigDecimal("25000"), 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenModelIsNull_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", null, "Sedan", new BigDecimal("25000"), 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenModelIsBlank_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "", "Sedan", new BigDecimal("25000"), 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenPriceIsZero_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "Camry", "Sedan", BigDecimal.ZERO, 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenPriceIsNegative_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "Camry", "Sedan", new BigDecimal("-10"), 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenQuantityIsNegative_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "Camry", "Sedan", new BigDecimal("25000"), -1);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenPriceIsNull_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "Camry", "Sedan", null, 10);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenQuantityIsNull_thenViolation() {
        VehicleRequestDTO dto = new VehicleRequestDTO("Toyota", "Camry", "Sedan", new BigDecimal("25000"), null);
        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }
}
