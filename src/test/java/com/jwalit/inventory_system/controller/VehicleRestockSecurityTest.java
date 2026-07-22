package com.jwalit.inventory_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwalit.inventory_system.dto.RestockRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security authorization tests for POST /api/vehicles/{id}/restock.
 * Uses standalone MockMvc with a manual role-enforcement interceptor —
 * no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class VehicleRestockSecurityTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController)
                .addInterceptors(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Object handler) throws Exception {
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
                .setControllerAdvice(new com.jwalit.inventory_system.exception.GlobalExceptionHandler())
                .build();
    }

    private void authenticateAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user", "password",
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role))
                )
        );
    }

    @Test
    void restock_adminUser_isAllowed() throws Exception {
        authenticateAs("ADMIN");

        org.mockito.Mockito.doNothing()
                .when(vehicleService).restock(org.mockito.ArgumentMatchers.anyLong(),
                        org.mockito.ArgumentMatchers.anyInt());

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RestockRequest(5))))
                .andExpect(status().isOk());

        SecurityContextHolder.clearContext();
    }

    @Test
    void restock_regularUser_isForbidden() throws Exception {
        authenticateAs("USER");

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RestockRequest(5))))
                .andExpect(status().isForbidden());

        SecurityContextHolder.clearContext();
    }

    @Test
    void restock_anonymousRequest_isUnauthorized() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/api/vehicles/1/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RestockRequest(5))))
                .andExpect(status().isUnauthorized());
    }
}
