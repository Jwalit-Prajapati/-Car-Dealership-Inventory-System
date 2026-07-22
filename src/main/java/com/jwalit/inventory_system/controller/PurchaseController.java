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
@RequestMapping("/api/vehicles")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<PurchaseResponseDTO> purchaseVehicle(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PurchaseResponseDTO response = purchaseService.purchaseVehicle(id, requestDTO, userDetails);
        return ResponseEntity.ok(response);
    }
}
