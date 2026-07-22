package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record VehicleRequestDTO(
    @NotBlank(message = "Make is required") String make,
    @NotBlank(message = "Model is required") String model,
    String category,
    @NotNull(message = "Price is required") @Positive(message = "Price must be positive") BigDecimal price,
    @NotNull(message = "Quantity is required") @PositiveOrZero(message = "Quantity must be zero or positive") Integer quantity
) {}
