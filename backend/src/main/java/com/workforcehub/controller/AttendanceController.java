package com.workforcehub.controller;

import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.entity.Attendance;
import com.workforcehub.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/check-in/{employeeId}")
    @Operation(summary = "Check in an employee")
    public ResponseEntity<ApiResponse<Attendance>> checkIn(@PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Checked in", attendanceService.checkIn(employeeId)));
    }

    @PostMapping("/check-out/{employeeId}")
    @Operation(summary = "Check out an employee")
    public ResponseEntity<ApiResponse<Attendance>> checkOut(@PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Checked out", attendanceService.checkOut(employeeId)));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get attendance by employee ID")
    public ResponseEntity<ApiResponse<Page<Attendance>>> getByEmployee(@PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByEmployee(employeeId, PageRequest.of(page, size))));
    }

    @GetMapping("/report/{employeeId}")
    @Operation(summary = "Get attendance report")
    public ResponseEntity<ApiResponse<List<Attendance>>> getReport(@PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceReport(employeeId, startDate, endDate)));
    }
}
