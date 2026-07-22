package com.jwalit.inventory_system.dto;

import com.jwalit.inventory_system.entity.Vehicle;

public class VehiclePurchaseCountDto {
    private Vehicle vehicle;
    private Long purchaseCount;

    public VehiclePurchaseCountDto(Vehicle vehicle, Long purchaseCount) {
        this.vehicle = vehicle;
        this.purchaseCount = purchaseCount;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Long getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(Long purchaseCount) {
        this.purchaseCount = purchaseCount;
    }
}
