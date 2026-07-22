package com.jwalit.inventory_system.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private Long id;
    private String make;
    private String model;
    private String category;
    private BigDecimal price;
    private Integer quantity;

}
