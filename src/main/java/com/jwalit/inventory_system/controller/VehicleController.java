package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.RestockRequest;
import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        VehicleResponseDTO created = vehicleService.create(vehicleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponseDTO>> getVehicles(
            @Valid com.jwalit.inventory_system.dto.PaginationRequest paginationRequest,
            Pageable pageable) {
        return ResponseEntity.ok(vehicleService.getVehicles(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VehicleResponseDTO>> searchVehicles(
            @Valid com.jwalit.inventory_system.dto.VehicleSearchRequest searchRequest,
            Pageable pageable) {
        
        return ResponseEntity.ok(vehicleService.searchVehicles(
                searchRequest.getMake(), 
                searchRequest.getModel(), 
                searchRequest.getCategory(), 
                searchRequest.getMinPrice(), 
                searchRequest.getMaxPrice(), 
                pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(@PathVariable Long id,
                                                             @Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        return ResponseEntity.ok(vehicleService.update(id, vehicleRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> restockVehicle(
            @PathVariable Long id,
            @Valid @RequestBody RestockRequest restockRequest) {
        vehicleService.restock(id, restockRequest.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Vehicle restocked successfully"));
    }
}
