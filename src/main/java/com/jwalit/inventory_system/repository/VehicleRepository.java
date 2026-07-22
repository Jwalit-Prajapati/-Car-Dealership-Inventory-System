package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Page<Vehicle> findAll(Pageable pageable);
    Page<Vehicle> findByMakeContainingIgnoreCase(String make, Pageable pageable);
    Page<Vehicle> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Vehicle> findByMakeContainingIgnoreCaseAndCategoryIgnoreCase(String make, String category, Pageable pageable);
}
