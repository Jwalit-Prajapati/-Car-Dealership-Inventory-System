package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.RestockRequest;
import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        Vehicle createdVehicle = vehicleService.create(vehicleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @GetMapping
    public ResponseEntity<Page<Vehicle>> getVehicles(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Pageable pageable) {
        if ((page != null && page < 0) || (size != null && size <= 0)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(vehicleService.getVehicles(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Vehicle>> searchVehicles(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) java.math.BigDecimal minimumPrice,
            @RequestParam(required = false) java.math.BigDecimal maximumPrice,
            Pageable pageable) {
            
        if (minimumPrice != null && minimumPrice.compareTo(java.math.BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (maximumPrice != null && maximumPrice.compareTo(java.math.BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (minimumPrice != null && maximumPrice != null && minimumPrice.compareTo(maximumPrice) > 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(vehicleService.searchVehicles(make, model, category, minimumPrice, maximumPrice, pageable));
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> updateVehicle(@org.springframework.web.bind.annotation.PathVariable Long id, @Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        return ResponseEntity.ok(vehicleService.update(id, vehicleRequestDTO));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
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
