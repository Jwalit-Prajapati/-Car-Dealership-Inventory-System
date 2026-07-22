package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface PurchaseService {
    PurchaseResponseDTO purchaseVehicle(Long vehicleId, PurchaseRequestDTO requestDTO, UserDetails userDetails);
}
