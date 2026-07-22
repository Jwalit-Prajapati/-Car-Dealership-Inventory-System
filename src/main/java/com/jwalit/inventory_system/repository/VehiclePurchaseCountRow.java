package com.jwalit.inventory_system.repository;

import java.math.BigDecimal;

public record VehiclePurchaseCountRow(
        Long vehicleId,
        String make,
        String model,
        String category,
        BigDecimal price,
        Integer quantity,
        Long purchaseCount
) {}
