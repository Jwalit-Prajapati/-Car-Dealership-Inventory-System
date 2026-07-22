package com.jwalit.inventory_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDTO {
    private Long purchaseId;
    private Long vehicleId;
    private String vehicleName;
    private Integer quantityPurchased;
    private BigDecimal purchasedPrice;
    private LocalDateTime purchaseDate;
    private String customerName;

}
