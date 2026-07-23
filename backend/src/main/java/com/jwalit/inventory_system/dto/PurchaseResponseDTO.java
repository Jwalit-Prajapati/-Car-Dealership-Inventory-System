package com.jwalit.inventory_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponseDTO(
    Long purchaseId,
    Long vehicleId,
    String vehicleName,
    Integer quantityPurchased,
    BigDecimal purchasedPrice,
    LocalDateTime purchaseDate,
    String customerName
) {}
