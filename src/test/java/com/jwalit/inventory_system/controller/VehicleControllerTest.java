package com.jwalit.inventory_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwalit.inventory_system.dto.VehicleRequestDTO;
import com.jwalit.inventory_system.entity.Vehicle;
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
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                        if (handler instanceof HandlerMethod handlerMethod) {
                            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
                            if (preAuthorize != null) {
                                var auth = SecurityContextHolder.getContext().getAuthentication();
                                if (auth == null || !preAuthorize.value().contains(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))) {
                                    response.setStatus(403);
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                })
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVehicle_asAdmin_returns201() throws Exception {
        manuallySetUserRole("ADMIN"); 

        VehicleRequestDTO request = createValidRequest();
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(1L);

        when(vehicleService.create(any(VehicleRequestDTO.class))).thenReturn(mockVehicle);

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
}
