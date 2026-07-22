package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.purchasedPrice * p.quantityPurchased) FROM Purchase p")
    BigDecimal getTotalRevenue();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) FROM Purchase p")
    Long getTotalSalesCount();

    @org.springframework.data.jpa.repository.Query("SELECT p.vehicle AS vehicle, COUNT(p) AS purchaseCount FROM Purchase p GROUP BY p.vehicle ORDER BY purchaseCount DESC")
    java.util.List<VehiclePurchaseCountProjection> findMostPurchasedVehicleData(org.springframework.data.domain.Pageable pageable);
}
