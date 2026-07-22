package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.dto.VehicleSearchRequest;
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

    public Page<VehicleResponseDTO> searchVehicles(VehicleSearchRequest request,
                                                    Pageable pageable) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecifications.hasMake(request.getMake()))
                .and(VehicleSpecifications.hasModel(request.getModel()))
                .and(VehicleSpecifications.hasCategory(request.getCategory()))
                .and(VehicleSpecifications.hasMinimumPrice(request.getMinPrice()))
                .and(VehicleSpecifications.hasMaximumPrice(request.getMaxPrice()));

        return vehicleRepository.findAll(spec, pageable)
                .map(vehicleMapper::toResponseDto);
    }

    @Transactional
    public VehicleResponseDTO update(Long id, VehicleRequestDTO vehicleRequestDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicleMapper.updateEntityFromDto(vehicleRequestDTO, vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }

}
