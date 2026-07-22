package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    @Query("""
            SELECT new com.jwalit.inventory_system.dto.InventoryStatsDto(
                COUNT(v),
                COALESCE(SUM(CASE WHEN v.quantity < 5 THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN v.quantity = 0 THEN 1 ELSE 0 END), 0)
            )
            FROM Vehicle v
            """)
    InventoryStatsDto getInventoryStatistics();
}
