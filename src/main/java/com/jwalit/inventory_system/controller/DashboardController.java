package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.service.InventoryStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
public class DashboardController {

    private final InventoryStatsService inventoryStatsService;
    private final com.jwalit.inventory_system.service.SalesStatisticsService salesStatisticsService;

    public DashboardController(InventoryStatsService inventoryStatsService, com.jwalit.inventory_system.service.SalesStatisticsService salesStatisticsService) {
        this.inventoryStatsService = inventoryStatsService;
        this.salesStatisticsService = salesStatisticsService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryStatsDto> getInventoryStats() {
        return ResponseEntity.ok(inventoryStatsService.getInventoryStats());
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.jwalit.inventory_system.dto.SalesStatisticsResponse> getSalesStats() {
        return ResponseEntity.ok(salesStatisticsService.getSalesStatistics());
    }
}
