package com.jwalit.inventory_system.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesStatisticsResponse {
    private BigDecimal totalRevenue;
    private Long totalSalesCount;
    private VehicleResponseDTO mostPurchasedVehicle;
}
