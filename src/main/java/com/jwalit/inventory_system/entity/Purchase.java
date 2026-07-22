package com.jwalit.inventory_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal purchasedPrice;
    private Integer quantityPurchased;
    private LocalDateTime purchaseDate;

    // Minimum needed for test compilation in phase 1 (red)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getPurchasedPrice() { return purchasedPrice; }
    public void setPurchasedPrice(BigDecimal purchasedPrice) { this.purchasedPrice = purchasedPrice; }

    public Integer getQuantityPurchased() { return quantityPurchased; }
    public void setQuantityPurchased(Integer quantityPurchased) { this.quantityPurchased = quantityPurchased; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public User getUser() { return null; }
    public void setUser(User user) {}

    public Vehicle getVehicle() { return null; }
    public void setVehicle(Vehicle vehicle) {}
}
