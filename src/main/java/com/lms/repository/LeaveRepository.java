package com.lms.repository;

import com.lms.model.LeaveRequest;
import com.lms.model.LeaveStatus;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LeaveRepository {
    private final Map<String, LeaveRequest> leaveRequests = new ConcurrentHashMap<>();
    
    public LeaveRequest save(LeaveRequest leaveRequest) {
        leaveRequests.put(leaveRequest.getRequestId(), leaveRequest);
        return leaveRequest;
    }
    
    public Optional<LeaveRequest> findById(String requestId) {
        return Optional.ofNullable(leaveRequests.get(requestId));
    }
    
    public List<LeaveRequest> findByEmployeeId(String employeeId) {
        return leaveRequests.values().stream()
                .filter(request -> request.getEmployeeId().equals(employeeId))
                .toList();
    }
    
    public List<LeaveRequest> findByStatus(LeaveStatus status) {
        return leaveRequests.values().stream()
                .filter(request -> request.getStatus() == status)
                .toList();
    }
    
    public List<LeaveRequest> findOverlappingLeaves(String employeeId, LocalDate startDate, LocalDate endDate) {
        return leaveRequests.values().stream()
                .filter(request -> request.getEmployeeId().equals(employeeId))
                .filter(request -> request.getStatus() == LeaveStatus.APPROVED || request.getStatus() == LeaveStatus.PENDING)
                .filter(request -> datesOverlap(request.getStartDate(), request.getEndDate(), startDate, endDate))
                .toList();
    }
    
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
    
    public List<LeaveRequest> findAll() {
        return new ArrayList<>(leaveRequests.values());
    }
}