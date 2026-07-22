package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.exception.InsufficientStockException;
import com.jwalit.inventory_system.exception.VehicleNotFoundException;
import com.jwalit.inventory_system.mapper.PurchaseMapper;
import com.jwalit.inventory_system.repository.PurchaseRepository;
import com.jwalit.inventory_system.repository.UserRepository;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PurchaseService {

    private final VehicleRepository vehicleRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final PurchaseMapper purchaseMapper;

    public PurchaseService(VehicleRepository vehicleRepository,
                                PurchaseRepository purchaseRepository,
                                UserRepository userRepository,
                                PurchaseMapper purchaseMapper) {
        this.vehicleRepository = vehicleRepository;
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Transactional
    public PurchaseResponseDTO purchaseVehicle(Long vehicleId, PurchaseRequestDTO requestDTO,
                                               UserDetails userDetails) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        if (vehicle.getQuantity() < requestDTO.getQuantity()) {
            throw new InsufficientStockException("Requested quantity exceeds available stock");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        vehicle.setQuantity(vehicle.getQuantity() - requestDTO.getQuantity());
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
