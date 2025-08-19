package com.lms.service;

import com.lms.model.*;
import com.lms.exception.LeaveManagementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class LeaveServiceTest {
    private EmployeeService employeeService;
    private LeaveService leaveService;
    private Employee testEmployee;
    
    @BeforeEach
    void setUp() throws LeaveManagementException {
        employeeService = new EmployeeService();
        leaveService = new LeaveService(employeeService);
        
        testEmployee = employeeService.addEmployee(
            "Test User", 
            "test@company.com", 
            "IT", 
            LocalDate.of(2024, 1, 1)
        );
    }
    
    @Test
    void testSuccessfulLeaveApplication() throws LeaveManagementException {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        
        LeaveRequest request = leaveService.applyForLeave(
            testEmployee.getEmployeeId(),
            startDate,
            endDate,
            "Personal work",
            LeaveType.CASUAL
        );
        
        assertNotNull(request);
        assertEquals(LeaveStatus.PENDING, request.getStatus());
        assertEquals(3, request.getNumberOfDays());
    }
    
    @Test
    void testLeaveApplicationBeforeJoiningDate() {
        LocalDate startDate = testEmployee.getJoiningDate().minusDays(1);
        LocalDate endDate = testEmployee.getJoiningDate().plusDays(1);
        
        Exception exception = assertThrows(LeaveManagementException.class, () -> {
            leaveService.applyForLeave(
                testEmployee.getEmployeeId(),
                startDate,
                endDate,
                "Invalid leave",
                LeaveType.CASUAL
            );
        });
        
        assertTrue(exception.getMessage().contains("before joining date"));
    }
    
    @Test
    void testInsufficientLeaveBalance() throws LeaveManagementException {
        // Apply for more days than available
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(50); // More than available balance
        
        Exception exception = assertThrows(LeaveManagementException.class, () -> {
            leaveService.applyForLeave(
                testEmployee.getEmployeeId(),
                startDate,
                endDate,
                "Long vacation",
                LeaveType.ANNUAL
            );
        });
        
        assertTrue(exception.getMessage().contains("Insufficient leave balance"));
    }
    
    @Test
    void testOverlappingLeaveRequests() throws LeaveManagementException {
        LocalDate startDate1 = LocalDate.now().plusDays(1);
        LocalDate endDate1 = LocalDate.now().plusDays(3);
        
        // First leave request
        leaveService.applyForLeave(
            testEmployee.getEmployeeId(),
            startDate1,
            endDate1,
            "First leave",
            LeaveType.CASUAL
        );
        
        // Overlapping leave request
        LocalDate startDate2 = LocalDate.now().plusDays(2);
        LocalDate endDate2 = LocalDate.now().plusDays(4);
        
        Exception exception = assertThrows(LeaveManagementException.class, () -> {
            leaveService.applyForLeave(
                testEmployee.getEmployeeId(),
                startDate2,
                endDate2,
                "Overlapping leave",
                LeaveType.CASUAL
            );
        });
        
        assertTrue(exception.getMessage().contains("overlaps"));
    }
    
    @Test
    void testInvalidDateRange() {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(2); // End before start
        
        Exception exception = assertThrows(LeaveManagementException.class, () -> {
            leaveService.applyForLeave(
                testEmployee.getEmployeeId(),
                startDate,
                endDate,
                "Invalid dates",
                LeaveType.CASUAL
            );
        });
        
        assertTrue(exception.getMessage().contains("Start date cannot be after end date"));
    }
}