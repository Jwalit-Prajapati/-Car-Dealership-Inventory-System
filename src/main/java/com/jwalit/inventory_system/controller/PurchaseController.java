package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import com.jwalit.inventory_system.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> purchaseVehicle(
            @Valid @RequestBody PurchaseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PurchaseResponseDTO response = purchaseService.purchaseVehicle(requestDTO, userDetails);
        return ResponseEntity.ok(response);
    }
}
