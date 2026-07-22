package com.jwalit.inventory_system.dto;

import jakarta.validation.constraints.Positive;

public class RestockRequest {

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    public RestockRequest() {
    }

    public RestockRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
