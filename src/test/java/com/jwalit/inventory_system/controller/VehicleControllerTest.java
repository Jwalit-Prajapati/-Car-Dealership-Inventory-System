package com.jwalit.inventory_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.dto.VehicleResponseDTO;
import com.jwalit.inventory_system.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController)
                .addInterceptors(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                        if (handler instanceof HandlerMethod handlerMethod) {
                            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
                            if (preAuthorize != null) {
                                var auth = SecurityContextHolder.getContext().getAuthentication();
                                if (auth == null) {
                                    response.setStatus(401);
                                    return false;
                                }
                                String role = auth.getAuthorities().iterator().next()
                                        .getAuthority().replace("ROLE_", "");
                                if (!preAuthorize.value().contains(role)) {
                                    response.setStatus(403);
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                })
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new com.jwalit.inventory_system.exception.GlobalExceptionHandler())
                .build();
    }

    private void manuallySetUserRole(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", "password",
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)))
        );
    }

    private VehicleRequestDTO createValidRequest() {
        return new VehicleRequestDTO("Toyota", "Camry", "Sedan", new BigDecimal("25000"), 10);
    }

    private VehicleResponseDTO createMockResponse(Long id) {
        return new VehicleResponseDTO(id, "Toyota", "Camry", "Sedan", new BigDecimal("25000"), 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVehicle_asAdmin_returns201() throws Exception {
        manuallySetUserRole("ADMIN");

        when(vehicleService.create(any(VehicleRequestDTO.class))).thenReturn(createMockResponse(1L));

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isCreated());

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "USER")
    void createVehicle_asUser_returns403() throws Exception {
        manuallySetUserRole("USER");

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isForbidden());

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVehicle_invalidPayload_returns400() throws Exception {
        manuallySetUserRole("ADMIN");

        VehicleRequestDTO request = new VehicleRequestDTO("", "", "", new BigDecimal("-1"), -5);

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getVehicles_returnsPaginatedVehicles() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getVehicles_invalidPagination_returns400() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("page", "-1")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchVehicles_byMake_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("make", "Toyota")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_byCategory_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("category", "Sedan")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_byMakeAndCategory_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("make", "Toyota")
                .param("category", "Sedan")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_byModel_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("model", "Camry")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_byMinimumPrice_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("minPrice", "15000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_byMaximumPrice_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("maxPrice", "25000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_withCombinedFilters_returns200() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("make", "Toyota")
                .param("model", "Camry")
                .param("category", "Sedan")
                .param("minPrice", "15000")
                .param("maxPrice", "35000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchVehicles_invalidMinPrice_returns400() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("minPrice", "-500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchVehicles_invalidMaxPrice_returns400() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("maxPrice", "-500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchVehicles_minGreaterThanMaxPrice_returns400() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("minPrice", "30000")
                .param("maxPrice", "20000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchVehicles_paginationWorksWithFilters() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                .param("make", "Toyota")
                .param("page", "1")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateVehicle_asAdmin_returns200() throws Exception {
        manuallySetUserRole("ADMIN");

        when(vehicleService.update(any(Long.class), any(VehicleRequestDTO.class)))
                .thenReturn(createMockResponse(1L));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateVehicle_invalidPayload_returns400() throws Exception {
        manuallySetUserRole("ADMIN");
        VehicleRequestDTO request = new VehicleRequestDTO("", "", "", new BigDecimal("-1"), -5);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateVehicle_asUser_returns403() throws Exception {
        manuallySetUserRole("USER");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isForbidden());
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateVehicle_notFound_returns404() throws Exception {
        manuallySetUserRole("ADMIN");

        when(vehicleService.update(any(Long.class), any(VehicleRequestDTO.class)))
                .thenThrow(new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/vehicles/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isNotFound());
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteVehicle_asAdmin_returns204() throws Exception {
        manuallySetUserRole("ADMIN");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteVehicle_asUser_returns403() throws Exception {
        manuallySetUserRole("USER");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/vehicles/1"))
                .andExpect(status().isForbidden());
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteVehicle_notFound_returns404() throws Exception {
        manuallySetUserRole("ADMIN");

        org.mockito.Mockito.doThrow(new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found"))
                .when(vehicleService).delete(any(Long.class));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/vehicles/999"))
                .andExpect(status().isNotFound());
        SecurityContextHolder.clearContext();
    }

    // ── Restock controller tests ───────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void restock_asAdmin_validRequest_returns200() throws Exception {
        manuallySetUserRole("ADMIN");

        com.jwalit.inventory_system.dto.RestockRequest request =
                new com.jwalit.inventory_system.dto.RestockRequest(10);

        org.mockito.Mockito.doNothing().when(vehicleService).restock(any(Long.class), any(Integer.class));

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vehicle restocked successfully"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "USER")
    void restock_asUser_returns403() throws Exception {
        manuallySetUserRole("USER");

        com.jwalit.inventory_system.dto.RestockRequest request =
                new com.jwalit.inventory_system.dto.RestockRequest(10);

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        SecurityContextHolder.clearContext();
    }

    @Test
    void restock_anonymous_returns401() throws Exception {
        SecurityContextHolder.clearContext();

        com.jwalit.inventory_system.dto.RestockRequest request =
                new com.jwalit.inventory_system.dto.RestockRequest(10);

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restock_zeroQuantity_returns400() throws Exception {
        manuallySetUserRole("ADMIN");

        com.jwalit.inventory_system.dto.RestockRequest request =
                new com.jwalit.inventory_system.dto.RestockRequest(0);

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restock_vehicleNotFound_returns404() throws Exception {
        manuallySetUserRole("ADMIN");

        com.jwalit.inventory_system.dto.RestockRequest request =
                new com.jwalit.inventory_system.dto.RestockRequest(5);

        org.mockito.Mockito.doThrow(new com.jwalit.inventory_system.exception.VehicleNotFoundException("Vehicle not found"))
                .when(vehicleService).restock(any(Long.class), any(Integer.class));

        mockMvc.perform(post("/api/vehicles/999/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        SecurityContextHolder.clearContext();
    }
}
