package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(p.purchasedPrice * p.quantityPurchased) FROM Purchase p")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(p) FROM Purchase p")
    Long getTotalSalesCount();

    @Query("SELECT new com.jwalit.inventory_system.repository.VehiclePurchaseCountRow(p.vehicle.id, p.vehicle.make, p.vehicle.model, p.vehicle.category, p.vehicle.price, p.vehicle.quantity, COUNT(p)) FROM Purchase p GROUP BY p.vehicle.id, p.vehicle.make, p.vehicle.model, p.vehicle.category, p.vehicle.price, p.vehicle.quantity ORDER BY COUNT(p) DESC")
    List<VehiclePurchaseCountRow> findMostPurchasedVehicleData(Pageable pageable);
}
