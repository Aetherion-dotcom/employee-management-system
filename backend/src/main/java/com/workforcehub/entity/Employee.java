package com.workforcehub.entity;

import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee entity representing an employee in the organization.
 */
@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_emp_employee_id", columnList = "employee_id"),
        @Index(name = "idx_emp_email", columnList = "email"),
        @Index(name = "idx_emp_department", columnList = "department_id"),
        @Index(name = "idx_emp_status", columnList = "status"),
        @Index(name = "idx_emp_joining_date", columnList = "joining_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Column(name = "employee_id", nullable = false, unique = true, length = 20)
    private String employeeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "tax_id", length = 30)
    private String taxId;

    @Column(name = "shift_start")
    private String shiftStart;

    @Column(name = "shift_end")
    private String shiftEnd;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> subordinates = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Attendance> attendanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payroll> payrollRecords = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    /**
     * Get full name of the employee.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
