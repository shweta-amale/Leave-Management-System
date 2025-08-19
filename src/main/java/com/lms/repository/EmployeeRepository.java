package com.lms.repository;

import com.lms.model.Employee;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeRepository {
    private final Map<String, Employee> employees = new ConcurrentHashMap<>();
    
    public Employee save(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
        return employee;
    }
    
    public Optional<Employee> findById(String employeeId) {
        return Optional.ofNullable(employees.get(employeeId));
    }
    
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }
    
    public List<Employee> findByDepartment(String department) {
        return employees.values().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .toList();
    }
    
    public boolean existsById(String employeeId) {
        return employees.containsKey(employeeId);
    }
    
    public void deleteById(String employeeId) {
        employees.remove(employeeId);
    }
    
    public long count() {
        return employees.size();
    }
}