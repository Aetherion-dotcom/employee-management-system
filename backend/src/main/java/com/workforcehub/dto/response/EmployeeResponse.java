package com.workforcehub.dto.response;

import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.enums.Gender;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employee response DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String designation;
    private LocalDate joiningDate;
    private EmployeeStatus status;
    private BigDecimal salary;
    private String profileImage;
    private String departmentName;
    private Long departmentId;
    private String managerName;
    private Long managerId;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String shiftStart;
    private String shiftEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
