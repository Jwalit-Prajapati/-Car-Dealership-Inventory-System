package com.jwalit.inventory_system.dto;

import java.math.BigDecimal;

public record SalesStatisticsResponse(
    BigDecimal totalRevenue,
    Long totalSalesCount,
    VehicleResponseDTO mostPurchasedVehicle
) {}
