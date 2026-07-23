package com.jwalit.inventory_system.dto;

import java.math.BigDecimal;

public record VehicleResponseDTO(
    Long id,
    String make,
    String model,
    String category,
    BigDecimal price,
    Integer quantity,
    String imageUrl
) {}
