package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    Vehicle create(VehicleRequestDTO vehicleRequestDTO);
    Page<Vehicle> getVehicles(Pageable pageable);
    Page<Vehicle> searchVehicles(String make, String category, Pageable pageable);
}
