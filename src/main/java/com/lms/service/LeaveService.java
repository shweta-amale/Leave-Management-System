package com.lms.service;

import com.lms.model.*;
import com.lms.repository.LeaveRepository;
import com.lms.exception.LeaveManagementException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final EmployeeService employeeService;
    
    public LeaveService(EmployeeService employeeService) {
        this.leaveRepository = new LeaveRepository();
        this.employeeService = employeeService;
    }
    
    public LeaveRequest applyForLeave(String employeeId, LocalDate startDate, LocalDate endDate, 
                                    String reason, LeaveType leaveType) throws LeaveManagementException {
        
        // Validate input
        validateLeaveApplication(employeeId, startDate, endDate, reason, leaveType);
        
        // Get employee and validate
        Employee employee = employeeService.getEmployee(employeeId);
        
        // Check if applying for leave before joining date
        if (startDate.isBefore(employee.getJoiningDate())) {
            throw new LeaveManagementException("Cannot apply for leave before joining date (" + 
                    employee.getJoiningDate() + ")");
        }
        
        // Calculate working days (excluding weekends)
        long requestedDays = calculateWorkingDays(startDate, endDate);
        
        // Check available balance
        if (requestedDays > employee.getAvailableLeaves()) {
            throw new LeaveManagementException(String.format(
                    "Insufficient leave balance. Requested: %d days, Available: %d days", 
                    requestedDays, employee.getAvailableLeaves()));
        }
        
        // Check for overlapping leaves
        List<LeaveRequest> overlappingLeaves = leaveRepository.findOverlappingLeaves(
                employeeId, startDate, endDate);
        
        if (!overlappingLeaves.isEmpty()) {
            throw new LeaveManagementException("Leave request overlaps with existing leave: " + 
                    overlappingLeaves.get(0).getRequestId());
        }
        
        // Create leave request
        String requestId = generateRequestId();
        LeaveRequest leaveRequest = new LeaveRequest(requestId, employeeId, startDate, endDate, reason, leaveType);
        
        return leaveRepository.save(leaveRequest);
    }
    
    private void validateLeaveApplication(String employeeId, LocalDate startDate, LocalDate endDate, 
                                        String reason, LeaveType leaveType) throws LeaveManagementException {
        
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new LeaveManagementException("Employee ID cannot be empty");
        }
        
        if (startDate == null || endDate == null) {
            throw new LeaveManagementException("Start date and end date cannot be null");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new LeaveManagementException("Start date cannot be after end date");
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            throw new LeaveManagementException("Cannot apply for leave in the past");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new LeaveManagementException("Leave reason cannot be empty");
        }
        
        if (leaveType == null) {
            throw new LeaveManagementException("Leave type must be specified");
        }
        
        // Check if leave is too far in future (more than 1 year)
        if (startDate.isAfter(LocalDate.now().plusYears(1))) {
            throw new LeaveManagementException("Cannot apply for leave more than 1 year in advance");
        }
        
        // Check maximum consecutive leave days (30 days)
        long requestedDays = calculateWorkingDays(startDate, endDate);
        if (requestedDays > 30) {
            throw new LeaveManagementException("Cannot apply for more than 30 consecutive working days");
        }
    }
    
    private long calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        long totalDays = 0;
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY && 
                current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                totalDays++;
            }
            current = current.plusDays(1);
        }
        
        return totalDays;
    }
    
    public LeaveRequest approveLeave(String requestId, String approvedBy) throws LeaveManagementException {
        LeaveRequest leaveRequest = leaveRepository.findById(requestId)
                .orElseThrow(() -> new LeaveManagementException("Leave request not found with ID: " + requestId));
        
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveManagementException("Leave request is not in pending status. Current status: " + 
                    leaveRequest.getStatus());
        }
        
        // Update employee's used leaves
        Employee employee = employeeService.getEmployee(leaveRequest.getEmployeeId());
        long leaveDays = calculateWorkingDays(leaveRequest.getStartDate(), leaveRequest.getEndDate());
        
        // Double-check balance before approval
        if (leaveDays > employee.getAvailableLeaves()) {
            throw new LeaveManagementException("Cannot approve - insufficient leave balance");
        }
        
        employee.setUsedLeaves(employee.getUsedLeaves() + (int) leaveDays);
        
        // Update leave request
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setApprovedBy(approvedBy);
        leaveRequest.setApprovedDate(LocalDate.now());
        
        return leaveRepository.save(leaveRequest);
    }
    
    public LeaveRequest rejectLeave(String requestId, String rejectedBy, String comments) 
            throws LeaveManagementException {
        
        LeaveRequest leaveRequest = leaveRepository.findById(requestId)
                .orElseThrow(() -> new LeaveManagementException("Leave request not found with ID: " + requestId));
        
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveManagementException("Leave request is not in pending status. Current status: " + 
                    leaveRequest.getStatus());
        }
        
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setApprovedBy(rejectedBy);
        leaveRequest.setApprovedDate(LocalDate.now());
        leaveRequest.setComments(comments);
        
        return leaveRepository.save(leaveRequest);
    }
    
    public List<LeaveRequest> getLeaveHistory(String employeeId) throws LeaveManagementException {
        // Validate employee exists
        employeeService.getEmployee(employeeId);
        return leaveRepository.findByEmployeeId(employeeId);
    }
    
    public List<LeaveRequest> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING);
    }
    
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRepository.findAll();
    }
    
    private String generateRequestId() {
        String id;
        do {
            id = "LR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (leaveRepository.findById(id).isPresent());
        return id;
    }
    
    public LeaveRequest cancelLeave(String requestId) throws LeaveManagementException {
        LeaveRequest leaveRequest = leaveRepository.findById(requestId)
                .orElseThrow(() -> new LeaveManagementException("Leave request not found with ID: " + requestId));
        
        if (leaveRequest.getStatus() == LeaveStatus.CANCELLED || 
            leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new LeaveManagementException("Cannot cancel leave request with status: " + 
                    leaveRequest.getStatus());
        }
        
        // If approved leave is being cancelled, restore leave balance
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED) {
            Employee employee = employeeService.getEmployee(leaveRequest.getEmployeeId());
            long leaveDays = calculateWorkingDays(leaveRequest.getStartDate(), leaveRequest.getEndDate());
            employee.setUsedLeaves(employee.getUsedLeaves() - (int) leaveDays);
        }
        
        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        return leaveRepository.save(leaveRequest);
    }
}