package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.Positive;

public record RestockRequest(
    @Positive(message = "Quantity must be greater than 0") Integer quantity
) {}
