package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;

public interface VehicleService {
    Vehicle create(VehicleRequestDTO vehicleRequestDTO);
}
