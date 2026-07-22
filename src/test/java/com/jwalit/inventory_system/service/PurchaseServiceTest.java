package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.dto.PurchaseResponseDTO;
import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.exception.InsufficientStockException;
import com.jwalit.inventory_system.exception.VehicleNotFoundException;
import com.jwalit.inventory_system.mapper.PurchaseMapper;
import com.jwalit.inventory_system.repository.PurchaseRepository;
import com.jwalit.inventory_system.repository.UserRepository;
import com.jwalit.inventory_system.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseMapper purchaseMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private PurchaseService purchaseService;

    private Vehicle vehicle;
    private User user;
    private PurchaseRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setMake("Toyota");
        vehicle.setCategory("SUV");
        vehicle.setPrice(BigDecimal.valueOf(25000));
        vehicle.setQuantity(5);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");

        requestDTO = new PurchaseRequestDTO(2);
    }

    @Test
    void testPurchaseVehicle_Success() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PurchaseResponseDTO expectedDto = new PurchaseResponseDTO(
                100L, 1L, "Toyota null", 2, BigDecimal.valueOf(25000),
                LocalDateTime.now(), "Test User");
        when(purchaseMapper.toResponseDto(any(Purchase.class))).thenReturn(expectedDto);

        PurchaseResponseDTO result = purchaseService.purchaseVehicle(1L, requestDTO, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getPurchaseId()).isEqualTo(100L);
        assertThat(result.getQuantityPurchased()).isEqualTo(2);
        assertThat(result.getPurchasedPrice()).isEqualTo(BigDecimal.valueOf(25000));

        assertThat(vehicle.getQuantity()).isEqualTo(3);

        verify(vehicleRepository).save(vehicle);
        verify(purchaseRepository).save(any(Purchase.class));
        verify(purchaseMapper).toResponseDto(any(Purchase.class));
    }

    @Test
    void testPurchaseVehicle_VehicleNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () ->
            purchaseService.purchaseVehicle(99L, requestDTO, userDetails)
        );

        verify(purchaseRepository, never()).save(any());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void testPurchaseVehicle_InsufficientStock() {
        requestDTO.setQuantity(6);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(InsufficientStockException.class, () ->
            purchaseService.purchaseVehicle(1L, requestDTO, userDetails)
        );

        verify(purchaseRepository, never()).save(any());
        verify(vehicleRepository, never()).save(any());
    }
}
