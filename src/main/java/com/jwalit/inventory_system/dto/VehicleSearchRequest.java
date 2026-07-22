package com.jwalit.inventory_system.dto;

import com.jwalit.inventory_system.validation.ValidPriceRange;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@ValidPriceRange
public record VehicleSearchRequest(
    String make,
    String model,
    String category,

    @PositiveOrZero(message = "Minimum price must be zero or greater") BigDecimal minPrice,

    @PositiveOrZero(message = "Maximum price must be zero or greater") BigDecimal maxPrice
) {}
