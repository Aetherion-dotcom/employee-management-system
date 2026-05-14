package com.workforcehub.service;

import com.workforcehub.dto.request.DepartmentRequest;
import com.workforcehub.dto.response.DepartmentResponse;
import com.workforcehub.entity.Department;
import com.workforcehub.entity.Employee;
import com.workforcehub.exception.DuplicateResourceException;
import com.workforcehub.exception.ResourceNotFoundException;
import com.workforcehub.repository.DepartmentRepository;
import com.workforcehub.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    @Cacheable("departments")
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAllActive().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse createDepartment(DepartmentRequest req) {
        if (departmentRepository.existsByName(req.getName()))
            throw new DuplicateResourceException("Department name exists: " + req.getName());
        if (departmentRepository.existsByCode(req.getCode()))
            throw new DuplicateResourceException("Department code exists: " + req.getCode());
        Department dept = Department.builder().name(req.getName()).code(req.getCode().toUpperCase())
                .description(req.getDescription()).active(req.isActive()).build();
        if (req.getHeadId() != null) {
            dept.setHead(employeeRepository.findById(req.getHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", req.getHeadId())));
        }
        return mapToResponse(departmentRepository.save(dept));
    }

    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest req) {
        Department dept = findOrThrow(id);
        if (!dept.getName().equals(req.getName()) && departmentRepository.existsByName(req.getName()))
            throw new DuplicateResourceException("Department name exists: " + req.getName());
        dept.setName(req.getName());
        dept.setCode(req.getCode().toUpperCase());
        dept.setDescription(req.getDescription());
        dept.setActive(req.isActive());
        if (req.getHeadId() != null) {
            dept.setHead(employeeRepository.findById(req.getHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", req.getHeadId())));
        }
        return mapToResponse(departmentRepository.save(dept));
    }

    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartment(Long id) {
        Department dept = findOrThrow(id);
        dept.softDelete();
        departmentRepository.save(dept);
    }

    private Department findOrThrow(Long id) {
        return departmentRepository.findById(id).filter(d -> !d.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    private DepartmentResponse mapToResponse(Department d) {
        return DepartmentResponse.builder().id(d.getId()).name(d.getName()).code(d.getCode())
                .description(d.getDescription()).active(d.isActive())
                .headName(d.getHead() != null ? d.getHead().getFullName() : null)
                .headId(d.getHead() != null ? d.getHead().getId() : null)
                .employeeCount(d.getEmployeeCount()).createdAt(d.getCreatedAt()).build();
    }
}
