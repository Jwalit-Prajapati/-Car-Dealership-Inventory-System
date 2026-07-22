package com.jwalit.inventory_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatsDto {
    private long totalVehicles;
    private long lowStockVehicles;
    private long outOfStockVehicles;

}
