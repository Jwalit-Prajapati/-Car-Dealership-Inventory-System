package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.SalesStatisticsResponse;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
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

        List<Object[]> topVehicleData = purchaseRepository.findMostPurchasedVehicleData(org.springframework.data.domain.PageRequest.of(0, 1));
        if (topVehicleData != null && !topVehicleData.isEmpty()) {
            Object[] row = topVehicleData.get(0);
            Vehicle topVehicle = (Vehicle) row[0];
            response.setMostPurchasedVehicle(topVehicle);
        } else {
            response.setMostPurchasedVehicle(null);
        }

        return response;
    }
}
