package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.SalesStatisticsResponse;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesStatisticsService {

    private final PurchaseRepository purchaseRepository;

    @Transactional(readOnly = true)
    public SalesStatisticsResponse getSalesStatistics() {
        BigDecimal revenue = purchaseRepository.getTotalRevenue();
        Long count = purchaseRepository.getTotalSalesCount();

        List<com.jwalit.inventory_system.repository.VehiclePurchaseCountRow> topVehicleData =
                purchaseRepository.findMostPurchasedVehicleData(PageRequest.of(0, 1));

        VehicleResponseDTO topVehicle = null;
        if (topVehicleData != null && !topVehicleData.isEmpty()) {
            com.jwalit.inventory_system.repository.VehiclePurchaseCountRow row = topVehicleData.get(0);
            topVehicle = new VehicleResponseDTO(row.vehicleId(), row.make(), row.model(), row.category(), row.price(), row.quantity());
        }

        return new SalesStatisticsResponse(
                revenue != null ? revenue : BigDecimal.ZERO,
                count != null ? count : 0L,
                topVehicle
        );
    }
}
