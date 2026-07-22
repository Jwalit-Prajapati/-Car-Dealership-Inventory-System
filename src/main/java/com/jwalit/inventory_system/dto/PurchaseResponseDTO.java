package com.jwalit.inventory_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseResponseDTO {

    private Long purchaseId;
    private Long vehicleId;
    private String vehicleName;
    private Integer quantityPurchased;
    private BigDecimal purchasedPrice;
    private LocalDateTime purchaseDate;
    private String customerName;

    public PurchaseResponseDTO() {}

    public PurchaseResponseDTO(Long purchaseId, Long vehicleId, String vehicleName,
                                Integer quantityPurchased, BigDecimal purchasedPrice,
                                LocalDateTime purchaseDate, String customerName) {
        this.purchaseId = purchaseId;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.quantityPurchased = quantityPurchased;
        this.purchasedPrice = purchasedPrice;
        this.purchaseDate = purchaseDate;
        this.customerName = customerName;
    }

    public Long getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public Integer getQuantityPurchased() { return quantityPurchased; }
    public void setQuantityPurchased(Integer quantityPurchased) { this.quantityPurchased = quantityPurchased; }

    public BigDecimal getPurchasedPrice() { return purchasedPrice; }
    public void setPurchasedPrice(BigDecimal purchasedPrice) { this.purchasedPrice = purchasedPrice; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
