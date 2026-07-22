package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import com.jwalit.inventory_system.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;



    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> purchaseVehicle(
            @Valid @RequestBody PurchaseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PurchaseResponseDTO response = purchaseService.purchaseVehicle(requestDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
