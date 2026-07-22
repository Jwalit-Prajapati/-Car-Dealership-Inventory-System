package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {
    @NotNull(message = "Vehicle ID must not be null")
    private Long vehicleId;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

}
