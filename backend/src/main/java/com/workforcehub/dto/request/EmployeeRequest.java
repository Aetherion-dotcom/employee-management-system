package com.workforcehub.dto.request;

import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee create/update request DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Please provide a valid phone number")
    private String phone;

    private Gender gender;
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private EmployeeStatus status;

    @DecimalMin(value = "0.0", message = "Salary must be positive")
    private BigDecimal salary;

    @NotNull(message = "Department is required")
    private Long departmentId;

    private Long managerId;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String bankAccountNumber;
    private String bankName;
    private String taxId;
    private String shiftStart;
    private String shiftEnd;
}
