package com.jwalit.inventory_system.dto;

import com.jwalit.inventory_system.validation.ValidPriceRange;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ValidPriceRange
public class VehicleSearchRequest {
    private String make;
    private String model;
    private String category;
    
    @PositiveOrZero(message = "Minimum price must be zero or greater")
    private BigDecimal minPrice;
    
    @PositiveOrZero(message = "Maximum price must be zero or greater")
    private BigDecimal maxPrice;
}
