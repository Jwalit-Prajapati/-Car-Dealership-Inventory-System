package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequestDTO(
    @NotNull(message = "Vehicle ID must not be null") Long vehicleId,

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be positive") Integer quantity
) {}
