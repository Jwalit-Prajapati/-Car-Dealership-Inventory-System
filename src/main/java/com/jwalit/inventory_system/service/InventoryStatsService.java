package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryStatsService {

    private final VehicleRepository vehicleRepository;

    public InventoryStatsService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public InventoryStatsDto getInventoryStats() {
        return vehicleRepository.getInventoryStatistics();
    }
}
