package com.workforcehub.service;

import com.workforcehub.entity.Attendance;
import com.workforcehub.entity.Employee;
import com.workforcehub.enums.AttendanceStatus;
import com.workforcehub.exception.BadRequestException;
import com.workforcehub.exception.ResourceNotFoundException;
import com.workforcehub.repository.AttendanceRepository;
import com.workforcehub.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private static final LocalTime SHIFT_START = LocalTime.of(9, 0);

    public Attendance checkIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today);
        if (existing.isPresent() && existing.get().getCheckInTime() != null)
            throw new BadRequestException("Already checked in today");
        LocalDateTime now = LocalDateTime.now();
        boolean isLate = now.toLocalTime().isAfter(SHIFT_START);
        int lateMinutes = isLate ? (int) Duration.between(SHIFT_START, now.toLocalTime()).toMinutes() : 0;
        Attendance attendance = existing.orElse(Attendance.builder()
                .employee(employee).attendanceDate(today).build());
        attendance.setCheckInTime(now);
        attendance.setStatus(isLate ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);
        attendance.setLate(isLate);
        attendance.setLateMinutes(lateMinutes);
        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new BadRequestException("No check-in record found for today"));
        if (attendance.getCheckOutTime() != null)
            throw new BadRequestException("Already checked out today");
        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.calculateWorkHours();
        return attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public Page<Attendance> getAttendanceByEmployee(Long employeeId, Pageable pageable) {
        return attendanceRepository.findByEmployeeId(employeeId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceReport(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, start, end);
    }

    @Transactional(readOnly = true)
    public long countPresentToday() {
        return attendanceRepository.countPresentToday(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAttendanceTrend(LocalDate start, LocalDate end) {
        return attendanceRepository.getAttendanceTrend(start, end);
    }

    @Transactional(readOnly = true)
    public Optional<Attendance> getTodayAttendance(Long employeeId) {
        return attendanceRepository.findTodayAttendance(employeeId);
    }
}
