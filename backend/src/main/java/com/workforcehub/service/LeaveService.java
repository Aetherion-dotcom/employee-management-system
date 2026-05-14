package com.workforcehub.service;

import com.workforcehub.dto.request.LeaveRequestDto;
import com.workforcehub.entity.Employee;
import com.workforcehub.entity.LeaveRequest;
import com.workforcehub.enums.LeaveStatus;
import com.workforcehub.exception.BadRequestException;
import com.workforcehub.exception.ResourceNotFoundException;
import com.workforcehub.repository.EmployeeRepository;
import com.workforcehub.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public LeaveRequest applyLeave(Long employeeId, LeaveRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        if (dto.getEndDate().isBefore(dto.getStartDate()))
            throw new BadRequestException("End date must be after start date");
        LeaveRequest leave = LeaveRequest.builder().employee(employee).leaveType(dto.getLeaveType())
                .startDate(dto.getStartDate()).endDate(dto.getEndDate()).reason(dto.getReason())
                .status(LeaveStatus.PENDING).build();
        LeaveRequest saved = leaveRequestRepository.save(leave);
        auditService.log("SYSTEM", "LEAVE_APPLY", "LeaveRequest", saved.getId(),
                employee.getFullName() + " applied for " + dto.getLeaveType() + " leave");
        return saved;
    }

    public LeaveRequest approveLeave(Long leaveId, Long approverId) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveId));
        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new BadRequestException("Leave request is not pending");
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", approverId));
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(approver);
        return leaveRequestRepository.save(leave);
    }

    public LeaveRequest rejectLeave(Long leaveId, Long approverId, String reason) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveId));
        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new BadRequestException("Leave request is not pending");
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", approverId));
        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedBy(approver);
        leave.setRejectionReason(reason);
        return leaveRequestRepository.save(leave);
    }

    @Transactional(readOnly = true)
    public Page<LeaveRequest> getLeavesByEmployee(Long employeeId, Pageable pageable) {
        return leaveRequestRepository.findByEmployeeId(employeeId, pageable);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getPendingRequests() {
        return leaveRequestRepository.findPendingRequests();
    }

    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
    }
}
