package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryStatsService {

    private final VehicleRepository vehicleRepository;

    public InventoryStatsDto getInventoryStats() {
        return vehicleRepository.getInventoryStatistics();
    }
}
