package com.workforcehub.controller;

import com.workforcehub.dto.request.EmployeeRequest;
import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.dto.response.EmployeeResponse;
import com.workforcehub.dto.response.PagedResponse;
import com.workforcehub.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "CRUD APIs for Employee Management")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees with pagination and search")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAllEmployees(page, size, sortBy, sortDir, search)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeById(id)));
    }

    @GetMapping("/emp/{employeeId}")
    @Operation(summary = "Get employee by employee ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getByEmployeeId(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeByEmployeeId(employeeId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Create new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", employeeService.createEmployee(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee updated", employeeService.updateEmployee(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete employee (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
    }

    @PostMapping("/{id}/profile-image")
    @Operation(summary = "Upload profile image")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws Exception {
        String url = employeeService.uploadProfileImage(id, file);
        return ResponseEntity.ok(ApiResponse.success("Profile image uploaded", url));
    }
}
