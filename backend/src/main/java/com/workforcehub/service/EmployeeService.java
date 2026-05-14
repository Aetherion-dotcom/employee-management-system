package com.workforcehub.service;

import com.workforcehub.dto.request.EmployeeRequest;
import com.workforcehub.dto.response.EmployeeResponse;
import com.workforcehub.dto.response.PagedResponse;
import com.workforcehub.entity.Department;
import com.workforcehub.entity.Employee;
import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.exception.DuplicateResourceException;
import com.workforcehub.exception.ResourceNotFoundException;
import com.workforcehub.repository.DepartmentRepository;
import com.workforcehub.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for employee CRUD operations and business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;

    /**
     * Get all employees with pagination, sorting, and filtering.
     */
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employeePage;
        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.searchEmployees(search.trim(), pageable);
        } else {
            employeePage = employeeRepository.findAllActive(pageable);
        }

        return PagedResponse.<EmployeeResponse>builder()
                .content(employeePage.getContent().stream().map(this::mapToResponse).toList())
                .pageNumber(employeePage.getNumber())
                .pageSize(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .first(employeePage.isFirst())
                .last(employeePage.isLast())
                .empty(employeePage.isEmpty())
                .build();
    }

    /**
     * Get employee by ID.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return mapToResponse(employee);
    }

    /**
     * Get employee by employee ID string.
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return mapToResponse(employee);
    }

    /**
     * Create a new employee.
     */
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        // Generate employee ID
        long count = employeeRepository.countActive() + 1;
        String employeeId = String.format("EMP%03d", count);

        Employee employee = Employee.builder()
                .employeeId(employeeId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .status(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE)
                .salary(request.getSalary())
                .department(department)
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .taxId(request.getTaxId())
                .shiftStart(request.getShiftStart())
                .shiftEnd(request.getShiftEnd())
                .build();

        if (request.getManagerId() != null) {
            Employee manager = findEmployeeOrThrow(request.getManagerId());
            employee.setManager(manager);
        }

        Employee saved = employeeRepository.save(employee);
        auditService.log("SYSTEM", "CREATE", "Employee", saved.getId(), "Employee created: " + saved.getFullName());
        log.info("Employee created: {} ({})", saved.getFullName(), saved.getEmployeeId());

        return mapToResponse(saved);
    }

    /**
     * Update an existing employee.
     */
    @CacheEvict(value = "employees", key = "#id")
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);

        // Check email uniqueness
        if (!employee.getEmail().equals(request.getEmail()) && employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setGender(request.getGender());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setAddress(request.getAddress());
        employee.setCity(request.getCity());
        employee.setState(request.getState());
        employee.setZipCode(request.getZipCode());
        employee.setCountry(request.getCountry());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setStatus(request.getStatus());
        employee.setSalary(request.getSalary());
        employee.setDepartment(department);
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setBankAccountNumber(request.getBankAccountNumber());
        employee.setBankName(request.getBankName());
        employee.setTaxId(request.getTaxId());
        employee.setShiftStart(request.getShiftStart());
        employee.setShiftEnd(request.getShiftEnd());

        if (request.getManagerId() != null) {
            Employee manager = findEmployeeOrThrow(request.getManagerId());
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        Employee saved = employeeRepository.save(employee);
        auditService.log("SYSTEM", "UPDATE", "Employee", saved.getId(), "Employee updated: " + saved.getFullName());

        return mapToResponse(saved);
    }

    /**
     * Soft-delete an employee.
     */
    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employee.softDelete();
        employee.setStatus(EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
        auditService.log("SYSTEM", "DELETE", "Employee", id, "Employee soft-deleted: " + employee.getFullName());
    }

    /**
     * Upload profile image for an employee.
     */
    @CacheEvict(value = "employees", key = "#id")
    public String uploadProfileImage(Long id, MultipartFile file) throws IOException {
        Employee employee = findEmployeeOrThrow(id);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("./uploads/profiles");
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        employee.setProfileImage("/uploads/profiles/" + fileName);
        employeeRepository.save(employee);

        return employee.getProfileImage();
    }

    // ===== Helper Methods =====

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .gender(employee.getGender())
                .dateOfBirth(employee.getDateOfBirth())
                .address(employee.getAddress())
                .city(employee.getCity())
                .state(employee.getState())
                .zipCode(employee.getZipCode())
                .country(employee.getCountry())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .status(employee.getStatus())
                .salary(employee.getSalary())
                .profileImage(employee.getProfileImage())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .managerName(employee.getManager() != null ? employee.getManager().getFullName() : null)
                .managerId(employee.getManager() != null ? employee.getManager().getId() : null)
                .emergencyContactName(employee.getEmergencyContactName())
                .emergencyContactPhone(employee.getEmergencyContactPhone())
                .shiftStart(employee.getShiftStart())
                .shiftEnd(employee.getShiftEnd())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
