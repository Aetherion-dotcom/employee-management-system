package com.workforcehub.service;

import com.workforcehub.dto.request.EmployeeRequest;
import com.workforcehub.dto.response.EmployeeResponse;
import com.workforcehub.dto.response.PagedResponse;
import com.workforcehub.entity.Department;
import com.workforcehub.entity.Employee;
import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.enums.Gender;
import com.workforcehub.exception.DuplicateResourceException;
import com.workforcehub.exception.ResourceNotFoundException;
import com.workforcehub.repository.DepartmentRepository;
import com.workforcehub.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private AuditService auditService;

    @InjectMocks private EmployeeService employeeService;

    private Employee testEmployee;
    private Department testDepartment;
    private EmployeeRequest testRequest;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder().name("Engineering").code("ENG").build();
        testDepartment.setId(1L);

        testEmployee = Employee.builder()
                .employeeId("EMP001").firstName("John").lastName("Doe")
                .email("john@test.com").designation("Developer")
                .joiningDate(LocalDate.now()).status(EmployeeStatus.ACTIVE)
                .salary(new BigDecimal("75000")).department(testDepartment)
                .gender(Gender.MALE).build();
        testEmployee.setId(1L);

        testRequest = EmployeeRequest.builder()
                .firstName("Jane").lastName("Smith").email("jane@test.com")
                .designation("Analyst").joiningDate(LocalDate.now())
                .departmentId(1L).salary(new BigDecimal("65000")).build();
    }

    @Test
    @DisplayName("Should return paginated employees")
    void getAllEmployees_ShouldReturnPaginatedList() {
        Page<Employee> page = new PageImpl<>(List.of(testEmployee));
        when(employeeRepository.findAllActive(any(Pageable.class))).thenReturn(page);

        PagedResponse<EmployeeResponse> result = employeeService.getAllEmployees(0, 10, "id", "asc", null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(employeeRepository).findAllActive(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return employee by ID")
    void getEmployeeById_ShouldReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        EmployeeResponse result = employeeService.getEmployeeById(1L);
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getEmployeeId()).isEqualTo("EMP001");
    }

    @Test
    @DisplayName("Should throw when employee not found")
    void getEmployeeById_ShouldThrowWhenNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.getEmployeeById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create employee successfully")
    void createEmployee_ShouldCreateAndReturn() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.countActive()).thenReturn(1L);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setId(2L);
            return e;
        });

        EmployeeResponse result = employeeService.createEmployee(testRequest);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw on duplicate email")
    void createEmployee_ShouldThrowOnDuplicateEmail() {
        when(employeeRepository.existsByEmail("jane@test.com")).thenReturn(true);
        assertThatThrownBy(() -> employeeService.createEmployee(testRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Should soft-delete employee")
    void deleteEmployee_ShouldSoftDelete() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        employeeService.deleteEmployee(1L);

        assertThat(testEmployee.isDeleted()).isTrue();
        assertThat(testEmployee.getStatus()).isEqualTo(EmployeeStatus.TERMINATED);
        verify(employeeRepository).save(testEmployee);
    }
}
