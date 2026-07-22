package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.repository.VehicleSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Transactional
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
                                                    BigDecimal minPrice,
                                                    BigDecimal maxPrice,
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

    @Transactional
    public VehicleResponseDTO update(Long id, VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleRepository.getVehicleOrThrow(id);

        vehicleMapper.updateEntityFromDto(vehicleRequestDTO, vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.getVehicleOrThrow(id);
        vehicleRepository.delete(vehicle);
    }

    @Transactional
    public void restock(Long id, int quantity) {
        Vehicle vehicle = vehicleRepository.getVehicleOrThrow(id);
        vehicle.addStock(quantity);
        vehicleRepository.save(vehicle);
    }
}
