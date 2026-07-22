package com.jwalit.inventory_system.service.Impl;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.service.InventoryStatsService;
import org.springframework.stereotype.Service;

@Service
public class InventoryStatsServiceImpl implements InventoryStatsService {

    private final VehicleRepository vehicleRepository;

    public InventoryStatsServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public InventoryStatsDto getInventoryStats() {
        long total = vehicleRepository.count();
        long lowStock = vehicleRepository.countByQuantityLessThan(5);
        long outOfStock = vehicleRepository.countByQuantity(0);
        return new InventoryStatsDto(total, lowStock, outOfStock);
    }
}
