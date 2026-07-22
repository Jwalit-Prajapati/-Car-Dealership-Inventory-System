package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.Positive;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockRequest {

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

}
