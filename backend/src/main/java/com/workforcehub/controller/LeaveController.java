package com.workforcehub.controller;

import com.workforcehub.dto.request.LeaveRequestDto;
import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.entity.LeaveRequest;
import com.workforcehub.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
@Tag(name = "Leave Management")
public class LeaveController {
    private final LeaveService leaveService;

    @PostMapping("/employee/{employeeId}")
    @Operation(summary = "Apply for leave")
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(@PathVariable Long employeeId, @Valid @RequestBody LeaveRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Leave applied", leaveService.applyLeave(employeeId, dto)));
    }

    @PutMapping("/{leaveId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    @Operation(summary = "Approve leave request")
    public ResponseEntity<ApiResponse<LeaveRequest>> approve(@PathVariable Long leaveId, @RequestParam Long approverId) {
        return ResponseEntity.ok(ApiResponse.success("Leave approved", leaveService.approveLeave(leaveId, approverId)));
    }

    @PutMapping("/{leaveId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    @Operation(summary = "Reject leave request")
    public ResponseEntity<ApiResponse<LeaveRequest>> reject(@PathVariable Long leaveId, @RequestParam Long approverId, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success("Leave rejected", leaveService.rejectLeave(leaveId, approverId, body.get("reason"))));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get leaves by employee ID")
    public ResponseEntity<ApiResponse<Page<LeaveRequest>>> getByEmployee(@PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getLeavesByEmployee(employeeId, PageRequest.of(page, size))));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    @Operation(summary = "Get all pending leave requests")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getPending() {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getPendingRequests()));
    }
}
