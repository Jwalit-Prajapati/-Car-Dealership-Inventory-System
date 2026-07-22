package com.jwalit.inventory_system.controller;

import com.jwalit.inventory_system.dto.ApiResponse;
import com.jwalit.inventory_system.util.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_V1_BASE)
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Health check successful", "UP"));
    }
}
