package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.repository.VehicleSpecifications;
import com.jwalit.inventory_system.exception.VehicleNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public VehicleResponseDTO create(VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleRequestDTO);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    public Page<VehicleResponseDTO> getVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable)
                .map(vehicleMapper::toResponseDto);
    }

    public Page<VehicleResponseDTO> searchVehicles(String make, String model, String category,
                                                    java.math.BigDecimal minPrice,
                                                    java.math.BigDecimal maxPrice,
                                                    Pageable pageable) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecifications.hasMake(make))
                .and(VehicleSpecifications.hasModel(model))
                .and(VehicleSpecifications.hasCategory(category))
                .and(VehicleSpecifications.hasMinimumPrice(minPrice))
                .and(VehicleSpecifications.hasMaximumPrice(maxPrice));

        return vehicleRepository.findAll(spec, pageable)
                .map(vehicleMapper::toResponseDto);
    }

    public VehicleResponseDTO update(Long id, VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicleMapper.updateEntityFromDto(vehicleRequestDTO, vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }

    public void restock(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than 0");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicle.addStock(quantity);
        vehicleRepository.save(vehicle);
    }
}
