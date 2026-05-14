package com.workforcehub.dto.response;

import lombok.*;

import java.util.Map;

/**
 * Dashboard analytics response DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long presentToday;
    private long absentToday;
    private long onLeaveToday;
    private long pendingLeaveRequests;
    private long totalDepartments;
    private long newHiresThisMonth;
    private Map<String, Long> departmentDistribution;
    private Map<String, Long> attendanceTrend;
    private Map<String, Long> monthlyHiring;
    private Map<String, Double> salaryDistribution;
}
