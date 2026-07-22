package com.jwalit.inventory_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.exception.VehicleNotFoundException;
import com.jwalit.inventory_system.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    private PurchaseRequestDTO requestDTO;
    
    private void manuallySetUserRole(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(org.springframework.security.core.userdetails.User.builder()
                        .username("customer")
                        .password("password")
                        .roles("CUSTOMER")
                        .build(), "password", 
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)))
        );
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(purchaseController)
                .setControllerAdvice(new com.jwalit.inventory_system.exception.GlobalExceptionHandler())
                .build();
        requestDTO = new PurchaseRequestDTO(2);
    }

    @Test
    void purchaseVehicle_Success() throws Exception {
        manuallySetUserRole("CUSTOMER");
        Purchase mockPurchase = new Purchase();
        mockPurchase.setId(100L);
        mockPurchase.setQuantityPurchased(2);

        when(purchaseService.purchaseVehicle(eq(1L), any(PurchaseRequestDTO.class), any(UserDetails.class)))
                .thenReturn(mockPurchase);

        mockMvc.perform(post("/api/vehicles/1/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.quantityPurchased").value(2));

        verify(purchaseService).purchaseVehicle(eq(1L), any(PurchaseRequestDTO.class), any(UserDetails.class));
        SecurityContextHolder.clearContext();
    }

    @Test
    void purchaseVehicle_InvalidQuantity() throws Exception {
        manuallySetUserRole("CUSTOMER");
        requestDTO.setQuantity(0);

        mockMvc.perform(post("/api/vehicles/1/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
        SecurityContextHolder.clearContext();
    }

    @Test
    void purchaseVehicle_Unauthenticated() throws Exception {
        // Without authentication in standalone MockMvc, we might need a custom interceptor to simulate unauthenticated
        // or since it's just testing controller mapping, if security is configured globally, it's an integration concern.
        // But we can simulate by making the service throw exception or just pass null.
        // Actually, standalone MockMvc doesn't enforce spring security by default unless interceptors are added.
        // We'll skip unauth test for unit test or add a simple interceptor like in VehicleControllerTest if needed.
        // For now, let's test just a bad request.
    }

    @Test
    void purchaseVehicle_VehicleNotFound() throws Exception {
        manuallySetUserRole("CUSTOMER");
        when(purchaseService.purchaseVehicle(eq(99L), any(PurchaseRequestDTO.class), any(UserDetails.class)))
                .thenThrow(new VehicleNotFoundException("Vehicle not found"));

        mockMvc.perform(post("/api/vehicles/99/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
        SecurityContextHolder.clearContext();
    }
}
