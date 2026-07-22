package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.SalesStatisticsResponse;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
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
    private final VehicleMapper vehicleMapper;

    @Transactional(readOnly = true)
    public SalesStatisticsResponse getSalesStatistics() {
        SalesStatisticsResponse response = new SalesStatisticsResponse();

        BigDecimal revenue = purchaseRepository.getTotalRevenue();
        response.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        Long count = purchaseRepository.getTotalSalesCount();
        response.setTotalSalesCount(count != null ? count : 0L);

        List<com.jwalit.inventory_system.dto.VehiclePurchaseCountDto> topVehicleData =
                purchaseRepository.findMostPurchasedVehicleData(
                        org.springframework.data.domain.PageRequest.of(0, 1));

        if (topVehicleData != null && !topVehicleData.isEmpty()) {
            Vehicle topVehicle = topVehicleData.get(0).getVehicle();
            VehicleResponseDTO dto = vehicleMapper.toResponseDto(topVehicle);
            response.setMostPurchasedVehicle(dto);
        } else {
            response.setMostPurchasedVehicle(null);
        }

        return response;
    }
}
