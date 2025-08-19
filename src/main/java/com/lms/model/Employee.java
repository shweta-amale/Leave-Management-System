package com.lms.model;

import java.time.LocalDate;
import java.util.Objects;

public class Employee {
    private String employeeId;
    private String name;
    private String email;
    private String department;
    private LocalDate joiningDate;
    private int totalLeaveBalance;
    private int usedLeaves;
    
    // Constructors
    public Employee() {}
    
    public Employee(String employeeId, String name, String email, String department, LocalDate joiningDate) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.joiningDate = joiningDate;
        this.totalLeaveBalance = calculateInitialLeaveBalance(joiningDate);
        this.usedLeaves = 0;
    }
    
    private int calculateInitialLeaveBalance(LocalDate joiningDate) {
        // Calculate leave balance based on joining date (pro-rated for current year)
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int joiningYear = joiningDate.getYear();
        
        if (joiningYear < currentYear) {
            return 24; // Full year leave balance
        } else {
            // Pro-rate based on remaining months
            int remainingMonths = 12 - joiningDate.getMonthValue() + 1;
            return (24 * remainingMonths) / 12;
        }
    }
    
    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    
    public int getTotalLeaveBalance() { return totalLeaveBalance; }
    public void setTotalLeaveBalance(int totalLeaveBalance) { this.totalLeaveBalance = totalLeaveBalance; }
    
    public int getUsedLeaves() { return usedLeaves; }
    public void setUsedLeaves(int usedLeaves) { this.usedLeaves = usedLeaves; }
    
    public int getAvailableLeaves() {
        return totalLeaveBalance - usedLeaves;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
    
    @Override
    public String toString() {
        return String.format("Employee{id='%s', name='%s', email='%s', department='%s', joiningDate=%s, availableLeaves=%d}",
                employeeId, name, email, department, joiningDate, getAvailableLeaves());
    }
}