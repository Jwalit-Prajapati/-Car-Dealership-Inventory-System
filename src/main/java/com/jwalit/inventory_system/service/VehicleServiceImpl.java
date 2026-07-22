package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Override
    public Page<Vehicle> getVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Override
    public Page<Vehicle> searchVehicles(String make, String category, Pageable pageable) {
        if (make != null && !make.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
            return vehicleRepository.findByMakeContainingIgnoreCaseAndCategoryIgnoreCase(make.trim(), category.trim(), pageable);
        } else if (make != null && !make.trim().isEmpty()) {
            return vehicleRepository.findByMakeContainingIgnoreCase(make.trim(), pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            return vehicleRepository.findByCategoryIgnoreCase(category.trim(), pageable);
        } else {
            return vehicleRepository.findAll(pageable);
        }
    }
}
