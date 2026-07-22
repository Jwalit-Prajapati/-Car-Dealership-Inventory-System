package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.entity.Purchase;
import org.springframework.security.core.userdetails.UserDetails;

public interface PurchaseService {
    Purchase purchaseVehicle(Long vehicleId, PurchaseRequestDTO requestDTO, UserDetails userDetails);
}
