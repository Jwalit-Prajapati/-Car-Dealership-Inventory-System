package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.mapper.VehicleMapper;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private VehicleRequestDTO createValidRequest() {
        return new VehicleRequestDTO("Honda", "Civic", "Sedan", new BigDecimal("22000"), 5);
    }

    @Test
    void create_validRequest_savesAndReturnsVehicle() {
        VehicleRequestDTO request = createValidRequest();
        
        Vehicle mappedVehicle = new Vehicle();
        mappedVehicle.setMake(request.getMake());
        mappedVehicle.setModel(request.getModel());

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        savedVehicle.setMake("Honda");
        savedVehicle.setModel("Civic");
        savedVehicle.setCategory("Sedan");
        savedVehicle.setPrice(new BigDecimal("22000"));
        savedVehicle.setQuantity(5);

        when(vehicleMapper.toEntity(any(VehicleRequestDTO.class))).thenReturn(mappedVehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        Vehicle result = vehicleService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMake()).isEqualTo("Honda");
        
        verify(vehicleMapper).toEntity(any(VehicleRequestDTO.class));
        verify(vehicleRepository).save(any(Vehicle.class));
    }
    
    @Test
    void create_nullRequest_throwsException() {
        assertThatThrownBy(() -> vehicleService.create(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
