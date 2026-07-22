package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public Vehicle create(VehicleRequestDTO vehicleRequestDTO) {
        if (vehicleRequestDTO == null) {
            throw new IllegalArgumentException("VehicleRequestDTO cannot be null");
        }
        
        Vehicle vehicle = vehicleMapper.toEntity(vehicleRequestDTO);
        return vehicleRepository.save(vehicle);
    }
}
