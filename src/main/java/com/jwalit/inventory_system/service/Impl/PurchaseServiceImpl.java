package com.jwalit.inventory_system.service.Impl;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.service.PurchaseService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Override
    public Purchase purchaseVehicle(Long vehicleId, PurchaseRequestDTO requestDTO, UserDetails userDetails) {
        return null; // Implementation in next phase
    }
}
