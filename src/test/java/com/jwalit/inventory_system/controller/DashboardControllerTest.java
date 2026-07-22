package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.InventoryStatsDto;
import com.jwalit.inventory_system.service.InventoryStatsService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryStatsService inventoryStatsService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
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

    private void setRole(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", "password",
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)))
        );
    }

    @Test
    void getInventoryStats_asAdmin_returns200WithCorrectJson() throws Exception {
        setRole("ADMIN");

        InventoryStatsDto stats = new InventoryStatsDto(125L, 18L, 6L);
        when(inventoryStatsService.getInventoryStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVehicles").value(125))
                .andExpect(jsonPath("$.lowStockVehicles").value(18))
                .andExpect(jsonPath("$.outOfStockVehicles").value(6));

        SecurityContextHolder.clearContext();
    }

    @Test
    void getInventoryStats_asUser_returns403() throws Exception {
        setRole("USER");

        mockMvc.perform(get("/api/admin/stats/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getInventoryStats_anonymous_returns401() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/admin/stats/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getInventoryStats_emptyInventory_returnsZeros() throws Exception {
        setRole("ADMIN");

        InventoryStatsDto stats = new InventoryStatsDto(0L, 0L, 0L);
        when(inventoryStatsService.getInventoryStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVehicles").value(0))
                .andExpect(jsonPath("$.lowStockVehicles").value(0))
                .andExpect(jsonPath("$.outOfStockVehicles").value(0));

        SecurityContextHolder.clearContext();
    }
}
