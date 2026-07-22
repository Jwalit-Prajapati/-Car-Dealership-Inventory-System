package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    BigDecimal getTotalRevenue();

    Long getTotalSalesCount();

    java.util.List<Object[]> findMostPurchasedVehicleData();
}
