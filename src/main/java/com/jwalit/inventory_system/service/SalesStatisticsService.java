package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.SalesStatisticsResponse;
import com.jwalit.inventory_system.dto.VehiclePurchaseCountDto;
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
        SalesStatisticsResponse response = new SalesStatisticsResponse();

        BigDecimal revenue = purchaseRepository.getTotalRevenue();
        response.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        Long count = purchaseRepository.getTotalSalesCount();
        response.setTotalSalesCount(count != null ? count : 0L);

        List<VehiclePurchaseCountDto> topVehicleData =
                purchaseRepository.findMostPurchasedVehicleData(PageRequest.of(0, 1));

        if (topVehicleData != null && !topVehicleData.isEmpty()) {
            VehicleResponseDTO topVehicle = topVehicleData.get(0).getVehicle();
            response.setMostPurchasedVehicle(topVehicle);
        } else {
            response.setMostPurchasedVehicle(null);
        }

        return response;
    }
}
