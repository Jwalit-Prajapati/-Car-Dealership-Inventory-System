package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
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
    private VehicleService vehicleService;

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

        VehicleResponseDTO expectedDto = new VehicleResponseDTO(1L, "Honda", "Civic", "Sedan",
                new BigDecimal("22000"), 5);

        when(vehicleMapper.toEntity(any(VehicleRequestDTO.class))).thenReturn(mappedVehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(vehicleMapper.toResponseDto(savedVehicle)).thenReturn(expectedDto);

        VehicleResponseDTO result = vehicleService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMake()).isEqualTo("Honda");

        verify(vehicleMapper).toEntity(any(VehicleRequestDTO.class));
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(vehicleMapper).toResponseDto(savedVehicle);
    }

    @Test
    void update_validRequest_updatesAndReturnsVehicle() {
        VehicleRequestDTO request = createValidRequest();
        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(1L);
        existingVehicle.setMake("Old Make");

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        savedVehicle.setMake(request.getMake());

        VehicleResponseDTO expectedDto = new VehicleResponseDTO(1L, request.getMake(), "Civic", "Sedan",
                new BigDecimal("22000"), 5);

        when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(existingVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(vehicleMapper.toResponseDto(savedVehicle)).thenReturn(expectedDto);

        VehicleResponseDTO result = vehicleService.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getMake()).isEqualTo(request.getMake());
        verify(vehicleRepository).findById(1L);
        verify(vehicleMapper).updateEntityFromDto(request, existingVehicle);
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(vehicleMapper).toResponseDto(savedVehicle);
    }

    @Test
    void update_vehicleNotFound_throwsException() {
        VehicleRequestDTO request = createValidRequest();
        when(vehicleRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(999L, request))
            .isInstanceOf(com.jwalit.inventory_system.exception.VehicleNotFoundException.class);
    }

    @Test
    void delete_existingVehicle_deletesVehicle() {
        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(1L);

        when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(existingVehicle));

        vehicleService.delete(1L);

        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).delete(existingVehicle);
    }

    @Test
    void delete_vehicleNotFound_throwsException() {
        when(vehicleRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> vehicleService.delete(999L))
            .isInstanceOf(com.jwalit.inventory_system.exception.VehicleNotFoundException.class);
    }

    // ── Restock tests ──────────────────────────────────────────────────────────

    @Test
    void restock_validQuantity_incrementsAndSavesVehicle() {
        Vehicle existing = new Vehicle();
        existing.setId(1L);
        existing.setQuantity(5);

        Vehicle saved = new Vehicle();
        saved.setId(1L);
        saved.setQuantity(15);

        when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(saved);

        vehicleService.restock(1L, 10);

        assertThat(existing.getQuantity()).isEqualTo(15);
        verify(vehicleRepository).save(existing);
    }

    @Test
    void restock_largeQuantity_incrementsSafely() {
        Vehicle existing = new Vehicle();
        existing.setId(2L);
        existing.setQuantity(Integer.MAX_VALUE - 1);

        when(vehicleRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(existing);

        vehicleService.restock(2L, 1);

        assertThat(existing.getQuantity()).isEqualTo(Integer.MAX_VALUE);
        verify(vehicleRepository).save(existing);
    }

    @Test
    void restock_vehicleNotFound_throwsVehicleNotFoundException() {
        when(vehicleRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> vehicleService.restock(999L, 5))
            .isInstanceOf(com.jwalit.inventory_system.exception.VehicleNotFoundException.class);
    }

    @Test
    void restock_zeroQuantity_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> vehicleService.restock(1L, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void restock_negativeQuantity_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> vehicleService.restock(1L, -3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Search tests ───────────────────────────────────────────────────────────

    @Test
    void searchVehicles_withAllFilters_forwardsCorrectly() {
        org.springframework.data.domain.Page<Vehicle> emptyPage = org.springframework.data.domain.Page.empty();
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);

        vehicleService.searchVehicles("Toyota", "Camry", "Sedan", new BigDecimal("10000"), new BigDecimal("30000"), org.springframework.data.domain.PageRequest.of(0, 10));

        verify(vehicleRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void searchVehicles_withNoFilters_forwardsCorrectly() {
        org.springframework.data.domain.Page<Vehicle> emptyPage = org.springframework.data.domain.Page.empty();
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);
        vehicleService.searchVehicles(null, null, null, null, null, org.springframework.data.domain.PageRequest.of(0, 10));
        verify(vehicleRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void searchVehicles_withMinPriceOnly_forwardsCorrectly() {
        org.springframework.data.domain.Page<Vehicle> emptyPage = org.springframework.data.domain.Page.empty();
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);
        vehicleService.searchVehicles(null, null, null, new BigDecimal("10000"), null, org.springframework.data.domain.PageRequest.of(0, 10));
        verify(vehicleRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void searchVehicles_withMaxPriceOnly_forwardsCorrectly() {
        org.springframework.data.domain.Page<Vehicle> emptyPage = org.springframework.data.domain.Page.empty();
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);
        vehicleService.searchVehicles(null, null, null, null, new BigDecimal("30000"), org.springframework.data.domain.PageRequest.of(0, 10));
        verify(vehicleRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void searchVehicles_withBoundaryPrices_forwardsCorrectly() {
        org.springframework.data.domain.Page<Vehicle> emptyPage = org.springframework.data.domain.Page.empty();
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);
        vehicleService.searchVehicles(null, null, null, BigDecimal.ZERO, new BigDecimal("9999999"), org.springframework.data.domain.PageRequest.of(0, 10));
        verify(vehicleRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void searchVehicles_emptyResultsHandledCorrectly() {
        org.springframework.data.domain.Page<Vehicle> emptyPage = org.springframework.data.domain.Page.empty();
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);

        org.springframework.data.domain.Page<VehicleResponseDTO> result = vehicleService.searchVehicles("Unknown", null, null, null, null, org.springframework.data.domain.PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }
}
