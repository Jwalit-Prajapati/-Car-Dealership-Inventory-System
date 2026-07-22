package com.jwalit.inventory_system.service.Impl;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.service.VehicleService;
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
    public Page<Vehicle> searchVehicles(String make, String model, String category, java.math.BigDecimal minimumPrice, java.math.BigDecimal maximumPrice, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Vehicle> spec = (root, query, cb) -> cb.conjunction();
        if (make != null && !make.trim().isEmpty()) {
            spec = spec.and(com.jwalit.inventory_system.repository.VehicleSpecifications.hasMake(make));
        }
        if (model != null && !model.trim().isEmpty()) {
            spec = spec.and(com.jwalit.inventory_system.repository.VehicleSpecifications.hasModel(model));
        }
        if (category != null && !category.trim().isEmpty()) {
            spec = spec.and(com.jwalit.inventory_system.repository.VehicleSpecifications.hasCategory(category));
        }
        if (minimumPrice != null) {
            spec = spec.and(com.jwalit.inventory_system.repository.VehicleSpecifications.hasMinimumPrice(minimumPrice));
        }
        if (maximumPrice != null) {
            spec = spec.and(com.jwalit.inventory_system.repository.VehicleSpecifications.hasMaximumPrice(maximumPrice));
        }
        return vehicleRepository.findAll(spec, pageable);
    }

    @Override
    public Vehicle update(Long id, VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));
        
        vehicleMapper.updateEntityFromDto(vehicleRequestDTO, vehicle);
        
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }

    @Override
    public void restock(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than 0");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicle.addStock(quantity);
        vehicleRepository.save(vehicle);
    }
}
