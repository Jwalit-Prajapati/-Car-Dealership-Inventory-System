package com.jwalit.inventory_system.mapper;

import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import com.jwalit.inventory_system.entity.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PurchaseMapper {

    @Mapping(target = "purchaseId", source = "id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleName", expression = "java(purchase.getVehicle().getMake() + \" \" + purchase.getVehicle().getModel())")
    @Mapping(target = "customerName", expression = "java(purchase.getUser().getFirstName() + \" \" + purchase.getUser().getLastName())")
    PurchaseResponseDTO toResponseDto(Purchase purchase);
}
