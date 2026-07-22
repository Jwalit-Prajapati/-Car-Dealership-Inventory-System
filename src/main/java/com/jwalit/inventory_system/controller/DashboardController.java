package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.dto.SalesStatisticsResponse;
import com.jwalit.inventory_system.service.InventoryStatsService;
import com.jwalit.inventory_system.service.SalesStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class DashboardController {

    private final InventoryStatsService inventoryStatsService;
    private final SalesStatisticsService salesStatisticsService;


    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryStatsDto> getInventoryStats() {
        return ResponseEntity.ok(inventoryStatsService.getInventoryStats());
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalesStatisticsResponse> getSalesStats() {
        return ResponseEntity.ok(salesStatisticsService.getSalesStatistics());
    }
}
