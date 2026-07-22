package com.jwalit.inventory_system.dto;

public class InventoryStatsDto {

    private long totalVehicles;
    private long lowStockVehicles;
    private long outOfStockVehicles;

    public InventoryStatsDto(long totalVehicles, long lowStockVehicles, long outOfStockVehicles) {
        this.totalVehicles = totalVehicles;
        this.lowStockVehicles = lowStockVehicles;
        this.outOfStockVehicles = outOfStockVehicles;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public long getLowStockVehicles() {
        return lowStockVehicles;
    }

    public long getOutOfStockVehicles() {
        return outOfStockVehicles;
    }
}
