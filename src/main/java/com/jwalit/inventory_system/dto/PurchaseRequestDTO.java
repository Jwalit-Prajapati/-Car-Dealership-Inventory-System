package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PurchaseRequestDTO {
    
    @NotNull(message = "Vehicle ID must not be null")
    private Long vehicleId;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    public PurchaseRequestDTO() {}

    public PurchaseRequestDTO(Long vehicleId, Integer quantity) {
        this.vehicleId = vehicleId;
        this.quantity = quantity;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
