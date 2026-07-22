package com.jwalit.inventory_system.mapper;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {
    @Mapping(target = "id", ignore = true)
    Vehicle toEntity(VehicleRequestDTO dto);
}
