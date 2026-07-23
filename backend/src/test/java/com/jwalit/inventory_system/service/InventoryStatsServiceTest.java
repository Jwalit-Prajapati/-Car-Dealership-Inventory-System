package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryStatsServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private InventoryStatsService inventoryStatsService;

    @Test
    void getInventoryStats_returnsCorrectlyMappedDto() {
        InventoryStatsDto projected = new InventoryStatsDto(125L, 18L, 6L);
        when(vehicleRepository.getInventoryStatistics()).thenReturn(projected);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.totalVehicles()).isEqualTo(125L);
        assertThat(result.lowStockVehicles()).isEqualTo(18L);
        assertThat(result.outOfStockVehicles()).isEqualTo(6L);
        verify(vehicleRepository).getInventoryStatistics();
    }

    @Test
    void getInventoryStats_emptyInventory_returnsAllZeros() {
        InventoryStatsDto projected = new InventoryStatsDto(0L, 0L, 0L);
        when(vehicleRepository.getInventoryStatistics()).thenReturn(projected);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.totalVehicles()).isEqualTo(0L);
        assertThat(result.lowStockVehicles()).isEqualTo(0L);
        assertThat(result.outOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStats_allInStock_returnsZeroLowAndOutOfStock() {
        InventoryStatsDto projected = new InventoryStatsDto(50L, 0L, 0L);
        when(vehicleRepository.getInventoryStatistics()).thenReturn(projected);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.totalVehicles()).isEqualTo(50L);
        assertThat(result.lowStockVehicles()).isEqualTo(0L);
        assertThat(result.outOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStats_allLowStock_returnsCorrectCounts() {
        InventoryStatsDto projected = new InventoryStatsDto(10L, 10L, 0L);
        when(vehicleRepository.getInventoryStatistics()).thenReturn(projected);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.totalVehicles()).isEqualTo(10L);
        assertThat(result.lowStockVehicles()).isEqualTo(10L);
        assertThat(result.outOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStats_allOutOfStock_returnsCorrectCounts() {
        InventoryStatsDto projected = new InventoryStatsDto(8L, 8L, 8L);
        when(vehicleRepository.getInventoryStatistics()).thenReturn(projected);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.totalVehicles()).isEqualTo(8L);
        assertThat(result.lowStockVehicles()).isEqualTo(8L);
        assertThat(result.outOfStockVehicles()).isEqualTo(8L);
    }

    @Test
    void getInventoryStats_mixedInventory_returnsCorrectCounts() {
        InventoryStatsDto projected = new InventoryStatsDto(100L, 25L, 10L);
        when(vehicleRepository.getInventoryStatistics()).thenReturn(projected);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.totalVehicles()).isEqualTo(100L);
        assertThat(result.lowStockVehicles()).isEqualTo(25L);
        assertThat(result.outOfStockVehicles()).isEqualTo(10L);
    }

    @Test
    void getInventoryStats_invokesProjectionMethodOnce() {
        when(vehicleRepository.getInventoryStatistics()).thenReturn(new InventoryStatsDto(1L, 1L, 0L));

        inventoryStatsService.getInventoryStats();

        verify(vehicleRepository).getInventoryStatistics();
    }
}
