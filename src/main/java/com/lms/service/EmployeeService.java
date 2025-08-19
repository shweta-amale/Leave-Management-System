package com.lms.service;

import com.lms.model.Employee;
import com.lms.repository.EmployeeRepository;
import com.lms.exception.LeaveManagementException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    public EmployeeService() {
        this.employeeRepository = new EmployeeRepository();
    }
    
    public Employee addEmployee(String name, String email, String department, LocalDate joiningDate) 
            throws LeaveManagementException {
        
        // Validation
        validateEmployeeInput(name, email, department, joiningDate);
        
        // Check if email already exists
        if (employeeRepository.findAll().stream()
                .anyMatch(emp -> emp.getEmail().equalsIgnoreCase(email))) {
            throw new LeaveManagementException("Employee with email " + email + " already exists");
        }
        
        // Generate unique employee ID
        String employeeId = generateEmployeeId();
        
        Employee employee = new Employee(employeeId, name, email, department, joiningDate);
        return employeeRepository.save(employee);
    }
    
    private void validateEmployeeInput(String name, String email, String department, LocalDate joiningDate) 
            throws LeaveManagementException {
        
        if (name == null || name.trim().isEmpty()) {
            throw new LeaveManagementException("Employee name cannot be empty");
        }
        
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new LeaveManagementException("Invalid email format");
        }
        
        if (department == null || department.trim().isEmpty()) {
            throw new LeaveManagementException("Department cannot be empty");
        }
        
        if (joiningDate == null) {
            throw new LeaveManagementException("Joining date cannot be null");
        }
        
        if (joiningDate.isAfter(LocalDate.now())) {
            throw new LeaveManagementException("Joining date cannot be in the future");
        }
        
        // Check if joining date is too far in the past (more than 50 years)
        if (joiningDate.isBefore(LocalDate.now().minusYears(50))) {
            throw new LeaveManagementException("Invalid joining date - too far in the past");
        }
    }
    
    private String generateEmployeeId() {
        String id;
        do {
            id = "EMP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (employeeRepository.existsById(id));
        return id;
    }
    
    public Employee getEmployee(String employeeId) throws LeaveManagementException {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new LeaveManagementException("Employee not found with ID: " + employeeId));
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    public void updateLeaveBalance(String employeeId, int usedLeaves) throws LeaveManagementException {
        Employee employee = getEmployee(employeeId);
        employee.setUsedLeaves(usedLeaves);
        employeeRepository.save(employee);
    }
}