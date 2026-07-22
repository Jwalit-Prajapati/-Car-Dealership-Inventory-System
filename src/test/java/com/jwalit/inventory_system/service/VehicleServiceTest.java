package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
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

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void create_validRequest_savesAndReturnsVehicle() {
        VehicleRequestDTO request = new VehicleRequestDTO("Honda", "Civic", "Sedan", new BigDecimal("22000"), 5);
        
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        savedVehicle.setMake("Honda");
        savedVehicle.setModel("Civic");
        savedVehicle.setCategory("Sedan");
        savedVehicle.setPrice(new BigDecimal("22000"));
        savedVehicle.setQuantity(5);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        Vehicle result = vehicleService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMake()).isEqualTo("Honda");
        
        verify(vehicleRepository).save(any(Vehicle.class));
    }
    
    @Test
    void create_nullRequest_throwsException() {
        assertThatThrownBy(() -> vehicleService.create(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
