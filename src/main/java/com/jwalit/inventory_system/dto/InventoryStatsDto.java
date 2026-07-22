package com.jwalit.inventory_system.dto;

public record InventoryStatsDto(
    long totalVehicles,
    long lowStockVehicles,
    long outOfStockVehicles
) {}
