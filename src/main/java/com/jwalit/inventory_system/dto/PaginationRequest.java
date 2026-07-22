package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PaginationRequest {
    @PositiveOrZero(message = "Page index must not be less than zero")
    private Integer page;

    @Min(value = 1, message = "Page size must not be less than one")
    private Integer size;
}
