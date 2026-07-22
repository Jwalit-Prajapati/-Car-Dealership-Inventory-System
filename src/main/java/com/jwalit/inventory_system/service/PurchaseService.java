package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.exception.InsufficientStockException;

import com.jwalit.inventory_system.mapper.PurchaseMapper;
import com.jwalit.inventory_system.repository.PurchaseRepository;
import com.jwalit.inventory_system.repository.UserRepository;
import com.jwalit.inventory_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final VehicleRepository vehicleRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final PurchaseMapper purchaseMapper;

    @Transactional
    public PurchaseResponseDTO purchaseVehicle(PurchaseRequestDTO requestDTO,
                                               String email) {
        Vehicle vehicle = vehicleRepository.findById(requestDTO.getVehicleId())
                .orElseThrow(() -> new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found with id: " + requestDTO.getVehicleId()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        vehicle.reduceStock(requestDTO.getQuantity());
        vehicleRepository.save(vehicle);

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setVehicle(vehicle);
        purchase.setQuantityPurchased(requestDTO.getQuantity());
        purchase.setPurchasedPrice(vehicle.getPrice());
        purchase.setPurchaseDate(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        return purchaseMapper.toResponseDto(saved);
    }
}
