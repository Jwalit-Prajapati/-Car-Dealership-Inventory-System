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
    @Mapping(target = "vehicleName", source = "vehicle")
    @Mapping(target = "customerName", source = "user")
    PurchaseResponseDTO toResponseDto(Purchase purchase);

    default String toVehicleName(com.jwalit.inventory_system.entity.Vehicle vehicle) {
        if (vehicle == null) return null;
        return vehicle.getMake() + " " + vehicle.getModel();
    }

    default String toCustomerName(com.jwalit.inventory_system.entity.User user) {
        if (user == null) return null;
        return user.getFirstName() + " " + user.getLastName();
    }
}
