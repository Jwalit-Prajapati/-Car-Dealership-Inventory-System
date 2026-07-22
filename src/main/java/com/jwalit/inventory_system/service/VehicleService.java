package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    VehicleResponseDTO create(VehicleRequestDTO vehicleRequestDTO);
    Page<VehicleResponseDTO> getVehicles(Pageable pageable);
    Page<VehicleResponseDTO> searchVehicles(String make, String model, String category, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Pageable pageable);
    VehicleResponseDTO update(Long id, VehicleRequestDTO vehicleRequestDTO);
    void delete(Long id);
    void restock(Long id, int quantity);
}
