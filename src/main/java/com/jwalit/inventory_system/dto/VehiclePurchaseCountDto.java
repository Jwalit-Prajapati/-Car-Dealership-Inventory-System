package com.jwalit.inventory_system.dto;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePurchaseCountDto {
    private VehicleResponseDTO vehicle;
    private Long purchaseCount;
}
