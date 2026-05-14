package com.workforcehub.controller;

import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.dto.response.DashboardResponse;
import com.workforcehub.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard analytics APIs")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardData()));
    }
}
