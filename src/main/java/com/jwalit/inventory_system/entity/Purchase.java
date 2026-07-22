package com.jwalit.inventory_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Getter
@Setter
public class Purchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "purchased_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasedPrice;

    @Column(name = "quantity_purchased", nullable = false)
    private Integer quantityPurchased;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
}
