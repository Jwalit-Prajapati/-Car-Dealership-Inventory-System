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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private String category;
    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private Integer quantity;

    @jakarta.persistence.Version
    private Long version;


    public void addStock(int amount) {
        this.quantity = this.quantity + amount;
    }
}
