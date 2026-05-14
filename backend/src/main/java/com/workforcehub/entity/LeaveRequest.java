package com.workforcehub.entity;

import com.workforcehub.enums.LeaveStatus;
import com.workforcehub.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * LeaveRequest entity for tracking leave applications.
 */
@Entity
@Table(name = "leave_requests", indexes = {
        @Index(name = "idx_lr_employee", columnList = "employee_id"),
        @Index(name = "idx_lr_status", columnList = "status"),
        @Index(name = "idx_lr_dates", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "total_days")
    private Long totalDays;

    /**
     * Calculate total leave days.
     */
    @PrePersist
    @PreUpdate
    public void calculateTotalDays() {
        if (startDate != null && endDate != null) {
            this.totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }
}
