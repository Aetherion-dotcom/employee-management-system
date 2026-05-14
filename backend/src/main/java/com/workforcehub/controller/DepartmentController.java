package com.workforcehub.controller;

import com.workforcehub.dto.request.DepartmentRequest;
import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.dto.response.DepartmentResponse;
import com.workforcehub.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getAllDepartments()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getDepartmentById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(@Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Department created", departmentService.createDepartment(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Department updated", departmentService.updateDepartment(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted", null));
    }
}
