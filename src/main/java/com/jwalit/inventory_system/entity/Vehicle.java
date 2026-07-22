package com.jwalit.inventory_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_vehicle_make", columnList = "make"),
    @Index(name = "idx_vehicle_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false, length = 255)
    private String make;
    @jakarta.persistence.Column(nullable = false, length = 255)
    private String model;
    @jakarta.persistence.Column(nullable = false, length = 255)
    private String category;
    
    @Positive
    @jakarta.persistence.Column(nullable = false)
    private BigDecimal price;

    @PositiveOrZero
    @jakarta.persistence.Column(nullable = false)
    private Integer quantity;

    @jakarta.persistence.Version
    private Long version;


    public void addStock(int amount) {
        this.quantity = this.quantity + amount;
    }

    public void reduceStock(int amount) {
        if (this.quantity < amount) {
            throw new com.jwalit.inventory_system.exception.InsufficientStockException("Requested quantity exceeds available stock");
        }
        this.quantity = this.quantity - amount;
    }

    public String getDisplayName() {
        return this.make + " " + this.model;
    }
}
