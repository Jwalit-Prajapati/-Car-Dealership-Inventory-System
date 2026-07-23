package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public void restock(Long id, int quantity) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicle.addStock(quantity);
        vehicleRepository.save(vehicle);
    }
}
