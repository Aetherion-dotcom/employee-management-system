package com.workforcehub.entity;

import com.workforcehub.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance entity tracking daily check-in/check-out.
 */
@Entity
@Table(name = "attendance", indexes = {
        @Index(name = "idx_att_employee", columnList = "employee_id"),
        @Index(name = "idx_att_date", columnList = "attendance_date"),
        @Index(name = "idx_att_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_date", columnNames = {"employee_id", "attendance_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    @Column(name = "work_hours")
    private Double workHours;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "is_late", nullable = false)
    @Builder.Default
    private boolean late = false;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * Calculate work hours from check-in and check-out times.
     */
    public void calculateWorkHours() {
        if (checkInTime != null && checkOutTime != null) {
            Duration duration = Duration.between(checkInTime, checkOutTime);
            this.workHours = duration.toMinutes() / 60.0;
            // Standard work day is 8 hours
            if (this.workHours > 8.0) {
                this.overtimeHours = this.workHours - 8.0;
            } else {
                this.overtimeHours = 0.0;
            }
        }
    }
}
