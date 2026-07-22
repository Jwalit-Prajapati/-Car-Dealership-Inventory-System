package com.jwalit.inventory_system.dto;

import com.jwalit.inventory_system.validation.ValidPriceRange;
import jakarta.validation.constraints.Min;
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
    
    @PositiveOrZero(message = "Page index must not be less than zero")
    private Integer page;
    
    @Min(value = 1, message = "Page size must not be less than one")
    private Integer size;
}
