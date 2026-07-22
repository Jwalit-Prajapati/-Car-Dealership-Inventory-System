package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.service.Impl.InventoryStatsServiceImpl;
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
    private InventoryStatsServiceImpl inventoryStatsService;

    @Test
    void getInventoryStats_returnsCorrectlyMappedDto() {
        when(vehicleRepository.count()).thenReturn(125L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(18L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(6L);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.getTotalVehicles()).isEqualTo(125L);
        assertThat(result.getLowStockVehicles()).isEqualTo(18L);
        assertThat(result.getOutOfStockVehicles()).isEqualTo(6L);

        verify(vehicleRepository).count();
        verify(vehicleRepository).countByQuantityLessThan(5);
        verify(vehicleRepository).countByQuantity(0);
    }

    @Test
    void getInventoryStats_emptyInventory_returnsAllZeros() {
        when(vehicleRepository.count()).thenReturn(0L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(0L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(0L);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.getTotalVehicles()).isEqualTo(0L);
        assertThat(result.getLowStockVehicles()).isEqualTo(0L);
        assertThat(result.getOutOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStats_allInStock_returnsZeroLowAndOutOfStock() {
        when(vehicleRepository.count()).thenReturn(50L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(0L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(0L);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.getTotalVehicles()).isEqualTo(50L);
        assertThat(result.getLowStockVehicles()).isEqualTo(0L);
        assertThat(result.getOutOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStats_allLowStock_returnsCorrectCounts() {
        when(vehicleRepository.count()).thenReturn(10L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(10L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(0L);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.getTotalVehicles()).isEqualTo(10L);
        assertThat(result.getLowStockVehicles()).isEqualTo(10L);
        assertThat(result.getOutOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStats_allOutOfStock_returnsCorrectCounts() {
        when(vehicleRepository.count()).thenReturn(8L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(8L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(8L);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.getTotalVehicles()).isEqualTo(8L);
        assertThat(result.getLowStockVehicles()).isEqualTo(8L);
        assertThat(result.getOutOfStockVehicles()).isEqualTo(8L);
    }

    @Test
    void getInventoryStats_mixedInventory_returnsCorrectCounts() {
        when(vehicleRepository.count()).thenReturn(100L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(25L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(10L);

        InventoryStatsDto result = inventoryStatsService.getInventoryStats();

        assertThat(result.getTotalVehicles()).isEqualTo(100L);
        assertThat(result.getLowStockVehicles()).isEqualTo(25L);
        assertThat(result.getOutOfStockVehicles()).isEqualTo(10L);
    }

    @Test
    void getInventoryStats_invokesAllThreeRepositoryMethods() {
        when(vehicleRepository.count()).thenReturn(1L);
        when(vehicleRepository.countByQuantityLessThan(5)).thenReturn(1L);
        when(vehicleRepository.countByQuantity(0)).thenReturn(0L);

        inventoryStatsService.getInventoryStats();

        verify(vehicleRepository).count();
        verify(vehicleRepository).countByQuantityLessThan(5);
        verify(vehicleRepository).countByQuantity(0);
    }
}
